package com.yas.search.service;

import com.yas.search.document.Product;
import com.yas.search.repository.ProductRepository;
import com.yas.search.viewmodel.ProductListGetVm;
import com.yas.search.viewmodel.ProductListVm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductListGetVm findProductAdvance(String keyword, int page, int size) {
        Page<Product> productPage = productRepository.findAllByName(keyword, PageRequest.of(page, size));

        List<ProductListVm> productListVmList = productPage.getContent().stream().map(ProductListVm::fromModel).toList();

        return new ProductListGetVm(
                productListVmList,
                productPage.getNumber(),
                productPage.getSize(),
                (int) productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }
}
