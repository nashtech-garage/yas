package com.yas.inventory.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.StockExistingException;
import com.yas.inventory.constants.ApiConstant;
import com.yas.inventory.constants.MessageCode;
import com.yas.inventory.model.Stock;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.model.enumeration.FilterExistInWhSelection;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.product.ProductQuantityPostVm;
import com.yas.inventory.viewmodel.stock.StockPostVm;
import com.yas.inventory.viewmodel.stock.StockQuantityUpdateVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.stock.StockVm;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StockService {
    private final WarehouseRepository warehouseRepository;
    private final StockRepository stockRepository;
    private final ProductService productService;

    private final WarehouseService warehouseService;

    private final StockHistoryService stockHistoryService;

    public StockService(WarehouseRepository warehouseRepository,
                        StockRepository stockRepository,
                        ProductService productService,
                        WarehouseService warehouseService,
                        StockHistoryService stockHistoryService) {
        this.warehouseRepository = warehouseRepository;
        this.stockRepository = stockRepository;
        this.productService = productService;
        this.warehouseService = warehouseService;
        this.stockHistoryService = stockHistoryService;
    }

    public void addProductIntoWarehouse(List<StockPostVm> postVms) {

        List<Stock> stocks = postVms.stream().map(postVM -> {
            boolean existingInStock = stockRepository.existsByWarehouseIdAndProductId(
                    postVM.warehouseId(), postVM.productId()
            );

            if (existingInStock) {
                throw new StockExistingException(
                        MessageCode.STOCK_ALREADY_EXISTED, postVM.productId());
            }

            ProductInfoVm product = productService.getProduct(postVM.productId());

            if (product == null) {
                throw new NotFoundException(MessageCode.PRODUCT_NOT_FOUND, postVM.productId());
            }

            Optional<Warehouse> warehouseOp = warehouseRepository.findById(postVM.warehouseId());

            if (warehouseOp.isEmpty()) {
                throw new NotFoundException(MessageCode.WAREHOUSE_NOT_FOUND, postVM.warehouseId());
            }

            return Stock.builder()
                .productId(postVM.productId())
                .warehouse(warehouseOp.get())
                .quantity(0L)
                .reservedQuantity(0L)
                .build();
        }).toList();


        stockRepository.saveAll(stocks);

    }

    public List<StockVm> getStocksByWarehouseIdAndProductNameAndSku(Long warehouseId,
                                                                    String productName,
                                                                    String productSku) {
        HashMap<Long, ProductInfoVm> productInfoVmHashMap =
            (HashMap<Long, ProductInfoVm>) warehouseService.getProductWarehouse(
                    warehouseId,
                    productName,
                    productSku,
                    FilterExistInWhSelection.YES)
                .parallelStream()
                .collect(Collectors.toMap(ProductInfoVm::id, productInfoVm -> productInfoVm));

        List<Stock> stocks = stockRepository.findByWarehouseIdAndProductIdIn(
            warehouseId,
            productInfoVmHashMap.values().parallelStream().map(ProductInfoVm::id).toList()
        );

        return stocks.stream().map(
            stock -> {
                ProductInfoVm productInfoVm = productInfoVmHashMap.get(stock.getProductId());

                return StockVm.fromModel(stock, productInfoVm);
            }
        ).toList();
    }

    public void updateProductQuantityInStock(final StockQuantityUpdateVm requestBody) {
        List<StockQuantityVm> stockQuantityVms = requestBody.stockQuantityList();
        List<Stock> stocks =
            stockRepository.findAllById(stockQuantityVms.parallelStream().map(StockQuantityVm::stockId).toList());

        for (final Stock stock : stocks) {
            StockQuantityVm stockQuantityVm = stockQuantityVms
                .parallelStream()
                .filter(stockQuantityPostVm -> stockQuantityPostVm.stockId().equals(stock.getId()))
                .findFirst()
                .orElse(null);

            if (stockQuantityVm == null) {
                continue;
            }

            Long adjustedQuantity = stockQuantityVm.quantity() != null ? stockQuantityVm.quantity() : 0;

            if (adjustedQuantity < 0 && adjustedQuantity > stock.getQuantity()) {
                throw new BadRequestException(ApiConstant.INVALID_ADJUSTED_QUANTITY);
            }

            stock.setQuantity(stock.getQuantity() + adjustedQuantity);
        }
        stockRepository.saveAll(stocks);
        stockHistoryService.createStockHistories(stocks, stockQuantityVms);

        //Update stock quantity for product
        List<ProductQuantityPostVm> productQuantityPostVms = stocks.parallelStream()
            .map(ProductQuantityPostVm::fromModel)
            .toList();
        if (!productQuantityPostVms.isEmpty()) {
            productService.updateProductQuantity(productQuantityPostVms);
        }
    }
}
