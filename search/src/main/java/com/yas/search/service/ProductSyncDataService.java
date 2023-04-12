package com.yas.search.service;

import com.yas.search.config.ServiceUrlConfig;
import com.yas.search.viewmodel.ProductESDetailVm;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.core.ParameterizedTypeReference;
import com.yas.search.exception.NotFoundException;
import com.yas.search.document.Product;
import com.yas.search.constants.MessageCode;
import com.yas.search.repository.ProductRepository;

import java.util.List;
import java.net.URI;

@Service
public class ProductSyncDataService {
    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;
    private final ProductRepository productRepository;

    public ProductSyncDataService(WebClient webClient, ServiceUrlConfig serviceUrlConfig, ProductRepository productRepository) {
        this.webClient = webClient;
        this.serviceUrlConfig = serviceUrlConfig;
        this.productRepository = productRepository;
    }

    public ProductESDetailVm getProductESDetailById(Long id) {
        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.product()).path("/storefront/products-es/{id}").buildAndExpand(id).toUri();
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(ProductESDetailVm.class)
                .block();
    }

    public void updateProduct(Long id) {
        ProductESDetailVm productESDetailVm = getProductESDetailById(id);
        Product product = productRepository.findById(id).orElseThrow(()
                -> new NotFoundException(MessageCode.PRODUCT_NOT_FOUND, id));

        product.setName(productESDetailVm.name());
        product.setSlug(productESDetailVm.slug());
        product.setPrice(productESDetailVm.price());
        product.setIsPublished(productESDetailVm.isPublished());
        product.setIsVisibleIndividually(productESDetailVm.isVisibleIndividually());
        product.setIsAllowedToOrder(productESDetailVm.isAllowedToOrder());
        product.setIsFeatured(productESDetailVm.isFeatured());
        product.setThumbnailMediaId(productESDetailVm.thumbnailMediaId());
        product.setBrand(productESDetailVm.brand());
        product.setCategories(productESDetailVm.categories());
        product.setAttributes(productESDetailVm.attributes());
        productRepository.save(product);
    }

    public void createProduct(Long id) {
        ProductESDetailVm productESDetailVm = getProductESDetailById(id);

        Product product = Product.builder()
                .id(id)
                .name(productESDetailVm.name())
                .slug(productESDetailVm.slug())
                .price(productESDetailVm.price())
                .isPublished(productESDetailVm.isPublished())
                .isVisibleIndividually(productESDetailVm.isVisibleIndividually())
                .isAllowedToOrder(productESDetailVm.isAllowedToOrder())
                .isFeatured(productESDetailVm.isFeatured())
                .thumbnailMediaId(productESDetailVm.thumbnailMediaId())
                .brand(productESDetailVm.brand())
                .categories(productESDetailVm.categories())
                .attributes(productESDetailVm.attributes())
                .build();

        Product savedProduct = productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        final boolean isProductExisted = productRepository.existsById(id);
        if (!isProductExisted) {
            throw new NotFoundException(MessageCode.PRODUCT_NOT_FOUND, id);
        }

        productRepository.deleteById(id);
    }
}
