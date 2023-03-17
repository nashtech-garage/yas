package com.yas.product.service;

import com.yas.product.viewmodel.productattribute.ProductAttributeGroupVm;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.viewmodel.productattribute.ProductAttributeGroupListGetVm;
import com.yas.product.repository.ProductAttributeGroupRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductAttributeGroupService {
    private final ProductAttributeGroupRepository productAttributeGroupRepository;

    public ProductAttributeGroupService(ProductAttributeGroupRepository productAttributeGroupRepository) {
        this.productAttributeGroupRepository = productAttributeGroupRepository;
    }

    public ProductAttributeGroupListGetVm getPageableProductAttributeGroups(int pageNo, int pageSize) {
        List<ProductAttributeGroupVm> productAttributeGroupVms = new ArrayList<>();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ProductAttributeGroup> productAttributeGroupPage = productAttributeGroupRepository.findAll(pageable);
        List<ProductAttributeGroup> productAttributeGroups = productAttributeGroupPage.getContent();
        for (ProductAttributeGroup productAttributeGroup : productAttributeGroups) {
            productAttributeGroupVms.add(ProductAttributeGroupVm.fromModel(productAttributeGroup));
        }

        return new ProductAttributeGroupListGetVm(
            productAttributeGroupVms,
            productAttributeGroupPage.getNumber(),
            productAttributeGroupPage.getSize(),
            (int) productAttributeGroupPage.getTotalElements(),
            productAttributeGroupPage.getTotalPages(),
            productAttributeGroupPage.isLast()
        );
    }
}
