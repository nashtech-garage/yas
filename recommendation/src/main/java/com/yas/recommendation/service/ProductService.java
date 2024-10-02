package com.yas.recommendation.service;

import com.yas.recommendation.dto.ProductDetailDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProductService {

    @Autowired
    private RestTemplate restTemplate;

    public ProductDetailDTO getProductDetail(long productId) {
        return new ProductDetailDTO();
    }

    public String buildFormattedProduct(ProductDetailDTO productDetail) {
        return StringUtils.EMPTY;
    }
}
