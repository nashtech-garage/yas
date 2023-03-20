package com.yas.product.service;

import com.yas.product.viewmodel.productattribute.ProductAttributeGetVm;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.viewmodel.productattribute.ProductAttributeListGetVm;
import com.yas.product.repository.ProductAttributeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductAttributeService {
    private final ProductAttributeRepository productAttributeRepository;

    public ProductAttributeService(ProductAttributeRepository productAttributeRepository) {
        this.productAttributeRepository = productAttributeRepository;
    }

    public ProductAttributeListGetVm getPageableProductAttributes(int pageNo, int pageSize) {
        List<ProductAttributeGetVm> productAttributeGetVms = new ArrayList<>();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ProductAttribute> productAttributePage = productAttributeRepository.findAll(pageable);
        List<ProductAttribute> productAttributes = productAttributePage.getContent();
        for (ProductAttribute productAttribute : productAttributes) {
            productAttributeGetVms.add(ProductAttributeGetVm.fromModel(productAttribute));
        }

        return new ProductAttributeListGetVm(
            productAttributeGetVms,
            productAttributePage.getNumber(),
            productAttributePage.getSize(),
            (int) productAttributePage.getTotalElements(),
            productAttributePage.getTotalPages(),
            productAttributePage.isLast()
        );
    }
}
