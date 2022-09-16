package com.yas.product.model;
import com.yas.product.controller.BrandController;
import com.yas.product.exception.NotFoundException;
import com.yas.product.repository.BrandRepository;
import com.yas.product.viewmodel.BrandPostVm;
import com.yas.product.viewmodel.BrandVm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BrandControllerTest {

    private BrandRepository brandRepository;
    private BrandController brandController;

    private Brand brand1 = new Brand();


    @BeforeEach
    public void init(){
        brandRepository = mock(BrandRepository.class);
        brandController = new BrandController(brandRepository);
        brand1.setId(1L);
        brand1.setName("dien thoai");
        brand1.setSlug("dien-thoai");
    }

    @Test
    public void listBrands_ReturnList_Success() {
        Brand brand2 = new Brand();
        brand2.setId(2L);
        brand2.setName("ao quan");
        brand2.setSlug("ao-quan");
        List<Brand> brands= new ArrayList<>(Arrays.asList(brand1,brand2));
        when(brandRepository.findAll()).thenReturn(brands);
        ResponseEntity<List<BrandVm>> result = brandController.listBrands();
        assertThat(result.getStatusCode(),is(HttpStatus.OK));
        assertEquals(result.getBody().size(), brands.size());
        for(int i=0;i<brands.size();i++){
            assertEquals(result.getBody().get(i).slug(), brands.get(i).getSlug());
            assertEquals(result.getBody().get(i).name(), brands.get(i).getName());
        }
    }

    @Test
    public void getBrand_whenFindIdBrand_thenThrowException(){
        when(brandRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> brandController.getBrand(1L));
        assertThat(exception.getMessage(),is("Brand 1 is not found"));
    }
    @Test
    public void getBrand_whenFindIdBrand_thenSuccess(){
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand1));
        ResponseEntity<BrandVm> result = brandController.getBrand(1L);
        assertEquals(result.getBody().name(), brand1.getName());
        assertEquals(result.getBody().slug(), brand1.getSlug());
    }
    @Test
    public void createBrand_whenSaveBrandPostVm_thenSuccess(){
        BrandPostVm brandPostVm = new BrandPostVm("samsung","samsung");
        ResponseEntity<BrandVm> result = brandController.createBrand(brandPostVm, UriComponentsBuilder.fromPath("/brands/{id}"));
        assertEquals(result.getBody().name(), brandPostVm.name());
        assertEquals(result.getBody().slug(), brandPostVm.slug());
    }
    @Test
    public void updateBrand_whenFindIdBrandUpdate_thenThrowException(){
        BrandPostVm brandPostVm = new BrandPostVm("samsung","samsung");
        when(brandRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> brandController.updateBrand(1L,brandPostVm));
        assertThat(exception.getMessage(),is("Brand 1 is not found"));
    }

    @Test
    public void updateBrand_whenUpdateBrand_thenSuccess(){
        BrandPostVm brandPostVm = new BrandPostVm("samsung","samsung");
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand1));
        brandRepository.findById(1L).get().setSlug(brandPostVm.slug());
        brandRepository.findById(1L).get().setName(brandPostVm.name());
        when(brandRepository.save(brand1)).thenReturn(brand1);
        assertThat(brand1.getName(), is(brandPostVm.name()));
        assertThat(brand1.getSlug(), is(brandPostVm.slug()));
        ResponseEntity<Void> result = brandController.updateBrand(1L,brandPostVm);
        assertThat(result.getStatusCode(),is(HttpStatus.NO_CONTENT));
    }


}
