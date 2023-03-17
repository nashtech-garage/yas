package com.yas.product.service;

import com.yas.product.viewmodel.productoption.ProductOptionGetVm;
import com.yas.product.model.ProductOption;
import com.yas.product.viewmodel.productoption.ProductOptionListGetVm;
import com.yas.product.repository.ProductOptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductOptionService {
    private final ProductOptionRepository productOptionRepository;

    public ProductOptionService(ProductOptionRepository productOptionRepository) {
        this.productOptionRepository = productOptionRepository;
    }

    public ProductOptionListGetVm getPageableProductOptions(int pageNo, int pageSize) {
        List<ProductOptionGetVm> productOptionGetVms = new ArrayList<>();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ProductOption> productOptionPage = productOptionRepository.findAll(pageable);
        List<ProductOption> productOptions = productOptionPage.getContent();
        for (ProductOption productOption : productOptions) {
            productOptionGetVms.add(ProductOptionGetVm.fromModel(productOption));
        }

        return new ProductOptionListGetVm(
            productOptionGetVms,
            productOptionPage.getNumber(),
            productOptionPage.getSize(),
            (int) productOptionPage.getTotalElements(),
            productOptionPage.getTotalPages(),
            productOptionPage.isLast()
        );
    }
}
