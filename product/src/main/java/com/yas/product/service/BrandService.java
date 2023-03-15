package com.yas.product.service;

import com.yas.product.viewmodel.brand.BrandVm;
import com.yas.product.model.Brand;
import com.yas.product.viewmodel.brand.BrandListGetVm;
import com.yas.product.repository.BrandRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BrandService {
    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public BrandListGetVm getBrands(int pageNo, int pageSize) {
        List<BrandVm> brandVms = new ArrayList<>();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Brand> brandPage;

        brandPage = brandRepository.findAll(pageable);
        List<Brand> brandList = brandPage.getContent();
        for (Brand brand : brandList) {
            brandVms.add(BrandVm.fromModel(brand));
        }

        return new BrandListGetVm(
            brandVms,
            brandPage.getNumber(),
            brandPage.getSize(),
            (int) brandPage.getTotalElements(),
            brandPage.getTotalPages(),
            brandPage.isLast()
        );
    }

}
