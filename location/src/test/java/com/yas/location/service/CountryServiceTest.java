package com.yas.location.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.location.mapper.CountryMapper;
import com.yas.location.model.Country;
import com.yas.location.repository.CountryRepository;
import com.yas.location.viewmodel.country.CountryListGetVm;
import com.yas.location.viewmodel.country.CountryPostVm;
import com.yas.location.viewmodel.country.CountryVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryMapper countryMapper;

    @InjectMocks
    private CountryService countryService;

    private Country country;
    private CountryPostVm countryPostVm;

    @BeforeEach
    void setUp() {
        country = Country.builder()
            .id(1L)
            .name("Vietnam")
            .code2("VN")
            .code3("VNM")
            .build();
        
        countryPostVm = CountryPostVm.builder()
            .id("VN")
            .name("Vietnam")
            .code2("VN")
            .code3("VNM")
            .isBillingEnabled(true)
            .isShippingEnabled(true)
            .isCityEnabled(true)
            .isZipCodeEnabled(true)
            .isDistrictEnabled(true)
            .build();
    }

    @Test
    void findAllCountries_ShouldReturnListCountryVm() {
        when(countryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))).thenReturn(List.of(country));
        when(countryMapper.toCountryViewModelFromCountry(country)).thenReturn(CountryVm.fromModel(country));

        List<CountryVm> result = countryService.findAllCountries();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Vietnam");
    }

    @Test
    void findById_WhenIdExists_ShouldReturnCountryVm() {
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(countryMapper.toCountryViewModelFromCountry(country)).thenReturn(CountryVm.fromModel(country));

        CountryVm result = countryService.findById(1L);

        assertThat(result.name()).isEqualTo("Vietnam");
    }

    @Test
    void findById_WhenIdDoesNotExist_ShouldThrowNotFoundException() {
        when(countryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> countryService.findById(2L));
    }

    @Test
    void create_WhenValid_ShouldReturnCountry() {
        when(countryRepository.existsByCode2IgnoreCase("VN")).thenReturn(false);
        when(countryRepository.existsByNameIgnoreCase("Vietnam")).thenReturn(false);
        when(countryMapper.toCountryFromCountryPostViewModel(countryPostVm)).thenReturn(country);
        when(countryRepository.save(country)).thenReturn(country);

        Country result = countryService.create(countryPostVm);

        assertThat(result.getName()).isEqualTo("Vietnam");
    }

    @Test
    void create_WhenCode2Exists_ShouldThrowDuplicatedException() {
        when(countryRepository.existsByCode2IgnoreCase("VN")).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> countryService.create(countryPostVm));
    }

    @Test
    void create_WhenNameExists_ShouldThrowDuplicatedException() {
        when(countryRepository.existsByCode2IgnoreCase("VN")).thenReturn(false);
        when(countryRepository.existsByNameIgnoreCase("Vietnam")).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> countryService.create(countryPostVm));
    }

    @Test
    void update_WhenValid_ShouldUpdateCountry() {
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(countryRepository.existsByNameIgnoreCaseAndIdNot("Vietnam", 1L)).thenReturn(false);
        when(countryRepository.existsByCode2IgnoreCaseAndIdNot("VN", 1L)).thenReturn(false);

        countryService.update(countryPostVm, 1L);

        verify(countryMapper).toCountryFromCountryPostViewModel(country, countryPostVm);
        verify(countryRepository).save(country);
    }

    @Test
    void update_WhenCountryNotFound_ShouldThrowNotFoundException() {
        when(countryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> countryService.update(countryPostVm, 1L));
    }

    @Test
    void delete_WhenValid_ShouldDeleteCountry() {
        when(countryRepository.existsById(1L)).thenReturn(true);

        countryService.delete(1L);

        verify(countryRepository).deleteById(1L);
    }

    @Test
    void delete_WhenCountryNotFound_ShouldThrowNotFoundException() {
        when(countryRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> countryService.delete(1L));
    }

    @Test
    void getPageableCountries_ShouldReturnCountryListGetVm() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        Page<Country> page = new PageImpl<>(List.of(country), pageable, 1);
        when(countryRepository.findAll(pageable)).thenReturn(page);

        CountryListGetVm result = countryService.getPageableCountries(0, 10);

        assertThat(result.countryContent()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
    }
}
