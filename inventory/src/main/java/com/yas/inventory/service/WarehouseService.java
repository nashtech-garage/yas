package com.yas.inventory.service;

import com.yas.inventory.model.enumeration.FilterExistInWHSelection;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseVM;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final StockRepository stockRepository;
    private final ProductService productService;

    public List<WarehouseVM> getAll() {
        return warehouseRepository.findAll().stream().map(WarehouseVM::fromWarehouse).toList();
    }

    public List<ProductInfoVm> getProductWarehouse(
            Long warehouseId, String productName, String productSku, FilterExistInWHSelection existStatus) {
        List<Long> productIds = stockRepository.getProductIdsInWarehouse(warehouseId);


        List<ProductInfoVm> productVmList = productService.filterProducts(
                productName, productSku, productIds, existStatus);

        if (!CollectionUtils.isEmpty(productIds)) {
            return productVmList.stream().map(productVm ->
                new ProductInfoVm(productVm.id(), productVm.name(), productVm.sku(), productIds.contains(productVm.id()))
            ).toList();
        }


        return productVmList;
    }

}
