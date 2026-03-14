package com.yas.product.viewmodel;

import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.model.ProductOptionValue;
import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.viewmodel.category.CategoryListGetVm;
import com.yas.product.viewmodel.product.ProductDetailVm;
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
import com.yas.product.viewmodel.product.ProductEsDetailVm;
import com.yas.product.viewmodel.product.ProductGetDetailVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductInfoVm;
import com.yas.product.viewmodel.product.ProductOptionCombinationGetVm;
import com.yas.product.viewmodel.product.ProductOptionValueGetVm;
import com.yas.product.viewmodel.product.ProductOptionValueDisplay;
import com.yas.product.viewmodel.product.ProductThumbnailGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import com.yas.product.viewmodel.product.ProductListGetFromCategoryVm;
import com.yas.product.viewmodel.product.ProductFeatureGetVm;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductViewModelTest {

    @Test
    void productListVm_fromModel_shouldMapFields() {
        Product parent = Product.builder().id(2L).build();
        Product product = Product.builder()
            .id(1L)
            .name("Name")
            .slug("slug")
            .price(10.5)
            .taxClassId(5L)
            .parent(parent)
            .build();
        product.setCreatedOn(ZonedDateTime.now());
        product.setAllowedToOrder(true);
        product.setPublished(true);
        product.setFeatured(false);
        product.setVisibleIndividually(true);

        ProductListVm vm = ProductListVm.fromModel(product);

        assertThat(vm.id()).isEqualTo(1L);
        assertThat(vm.parentId()).isEqualTo(2L);
        assertThat(vm.price()).isEqualTo(10.5);
        assertThat(vm.slug()).isEqualTo("slug");
    }

    @Test
    void productGetDetailVm_shouldExposeFields() {
        Product product = Product.builder().id(3L).name("Prod").slug("prod").build();

        ProductGetDetailVm vm = ProductGetDetailVm.fromModel(product);

        assertThat(vm.id()).isEqualTo(3L);
        assertThat(vm.name()).isEqualTo("Prod");
        assertThat(vm.slug()).isEqualTo("prod");
    }

    @Test
    void productDetailVm_builder_shouldSetAllFields() {
        ProductDetailVm vm = ProductDetailVm.builder()
            .id(10L)
            .name("Detail")
            .slug("detail")
            .price(99.0)
            .parentId(4L)
            .build();

        assertThat(vm.id()).isEqualTo(10L);
        assertThat(vm.slug()).isEqualTo("detail");
        assertThat(vm.price()).isEqualTo(99.0);
        assertThat(vm.parentId()).isEqualTo(4L);
    }

    @Test
    void productThumbnailVm_shouldHoldValues() {
        ProductThumbnailVm vm = new ProductThumbnailVm(7L, "Thumb", "thumb", "url");
        ProductThumbnailGetVm getVm = new ProductThumbnailGetVm(7L, "Thumb", "thumb", "url", 12.0);

        assertThat(vm.id()).isEqualTo(7L);
        assertThat(getVm.thumbnailUrl()).isEqualTo("url");
        assertThat(getVm.price()).isEqualTo(12.0);
    }

    @Test
    void categoryVms_shouldExposeFields() {
        Category parent = new Category();
        parent.setId(1L);
        Category category = new Category();
        category.setId(2L);
        category.setName("Cat");
        category.setSlug("cat");
        category.setParent(parent);

        CategoryGetVm getVm = CategoryGetVm.fromModel(category);
        CategoryListGetVm listVm = new CategoryListGetVm(List.of(getVm), 0, 10, 1, 1, true);

        assertThat(getVm.parentId()).isEqualTo(1L);
        assertThat(listVm.categoryContent()).hasSize(1);
        assertThat(listVm.totalElements()).isEqualTo(1);
    }

    @Test
    void optionValue_and_combination_getVm_shouldMapFromModel() {
        ProductOption option = new ProductOption();
        option.setId(30L);
        option.setName("Size");
        ProductOptionValue pov = ProductOptionValue.builder()
            .id(40L)
            .productOption(option)
            .displayType("TEXT")
            .displayOrder(2)
            .value("M")
            .build();

        ProductOptionCombination combination = ProductOptionCombination.builder()
            .id(50L)
            .productOption(option)
            .value("M")
            .build();

        ProductOptionValueGetVm valueVm = ProductOptionValueGetVm.fromModel(pov);
        ProductOptionCombinationGetVm combVm = ProductOptionCombinationGetVm.fromModel(combination);

        assertThat(valueVm.productOptionId()).isEqualTo(30L);
        assertThat(valueVm.productOptionName()).isEqualTo("Size");
        assertThat(combVm.productOptionId()).isEqualTo(30L);
        assertThat(combVm.productOptionValue()).isEqualTo("M");
    }

    @Test
    void listAndFeatureVms_shouldExposePagingAndContent() {
        ProductThumbnailVm thumb = new ProductThumbnailVm(9L, "T", "t", "url");
        ProductListGetFromCategoryVm listVm = new ProductListGetFromCategoryVm(List.of(thumb), 0, 5, 1, 1, true);
        ProductFeatureGetVm featureVm = new ProductFeatureGetVm(List.of(new ProductThumbnailGetVm(9L, "T", "t", "url", 1.0)), 2);

        assertThat(listVm.productContent()).hasSize(1);
        assertThat(listVm.totalPages()).isEqualTo(1);
        assertThat(featureVm.totalPage()).isEqualTo(2);
        assertThat(featureVm.productList()).hasSize(1);
    }

    @Test
    void productDetailInfoVm_shouldHandleNullCollections() {
        ProductDetailInfoVm vm = new ProductDetailInfoVm(
            1L, "Name", "Short", "Desc", "Spec", "SKU", "GTIN", "slug",
            true, true, true, true, true, 10.0, 1L,
            null, "meta", "metaK", "metaD", 2L, "brand",
            null, null, null, null
        );

        assertThat(vm.getCategories()).isEmpty();
        assertThat(vm.getAttributeValues()).isEmpty();
        assertThat(vm.getVariations()).isEmpty();
    }

    @Test
    void productEsDetailVm_shouldExposeFields() {
        ProductEsDetailVm vm = new ProductEsDetailVm(
            1L, "Name", "slug", 10.0, true, true, true, false,
            5L, "Brand", List.of("cat1"), List.of("attr"));

        assertThat(vm.isFeatured()).isFalse();
        assertThat(vm.categories()).contains("cat1");
    }

    @Test
    void productInfoVm_shouldMapFromProduct() {
        Product product = Product.builder().id(6L).name("Info").sku("SKU-I").build();
        ProductInfoVm vm = ProductInfoVm.fromProduct(product);

        assertThat(vm.id()).isEqualTo(6L);
        assertThat(vm.sku()).isEqualTo("SKU-I");
    }

    @Test
    void productOptionValueDisplay_builder_shouldSetFields() {
        ProductOptionValueDisplay display = ProductOptionValueDisplay.builder()
            .productOptionId(11L)
            .displayType("TEXT")
            .displayOrder(1)
            .value("Val")
            .build();

        assertThat(display.productOptionId()).isEqualTo(11L);
        assertThat(display.value()).isEqualTo("Val");
    }
}
