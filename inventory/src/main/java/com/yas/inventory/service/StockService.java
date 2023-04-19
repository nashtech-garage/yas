package com.yas.inventory.service;

import com.yas.inventory.constants.MessageCode;
import com.yas.inventory.exception.NotFoundException;
import com.yas.inventory.exception.OutOfStockException;
import com.yas.inventory.model.Stock;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.error.OutStockErrorVm;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.stock.StockPostVM;
import com.yas.inventory.viewmodel.stock.StockRequest;
import com.yas.inventory.viewmodel.stock.StockVM;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Transactional
@RequiredArgsConstructor
public class StockService {
    private final WarehouseRepository warehouseRepository;
    private final StockRepository stockRepository;
    private final ProductService productService;
    public void addProductIntoWarehouse(List<StockPostVM> postVMs) {

        List<Stock> stocks = postVMs.stream().map(postVM -> {
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

    public List<StockVM> getStockAvalables(List<StockRequest> stockCheckRequests) {
        List<Long> productIds = stockCheckRequests.stream().map(StockRequest::productId).toList();
        List<Stock> stocks = stockRepository.getAvailableStockByProductIds(productIds);
        CopyOnWriteArrayList<Stock> availableStocks = new CopyOnWriteArrayList<>();
        // concurrency version of ArrayList is CopyOnWriteArrayList
        CopyOnWriteArrayList<OutStockErrorVm> errors = new CopyOnWriteArrayList<>();
        stockCheckRequests.parallelStream().forEach(scr -> {
            List<Stock> stockChecks = stocks.stream()
                    .filter(stock -> scr.productId().equals(stock.getProductId())).toList();
            Optional<Stock> stockOp = getAvalableStock(stockChecks, scr.quantity());
            if (stockOp.isEmpty()) {
                errors.add(new OutStockErrorVm(scr.productId(), scr.quantity()));
            } else {
                availableStocks.add(stockOp.get());
            }
        });

        if (!errors.isEmpty()) {
            throw new OutOfStockException(MessageCode.OUT_OF_STOCK, errors);
        }

        stockRepository.saveAll(stocks);

        return availableStocks.stream().map(StockVM::fromStock).toList();
    }

    private Optional<Stock> getAvalableStock(List<Stock> stocks, Long orderQuantity) {
        for (Stock stock: stocks) {
            Long availableQuantity = stock.getQuantity() - stock.getReservedQuantity();

            if (orderQuantity <= availableQuantity) {
                stock.setReservedQuantity(stock.getReservedQuantity() + orderQuantity);
                return Optional.of(stock);
            }
        }

        return Optional.empty();
    }
}
