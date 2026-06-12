package com.yas.location.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

class CountryServiceTest {
    private CountryRepository countryRepository;
    private CountryMapper countryMapper;
    private CountryService countryService;

    @BeforeEach
    void setUp() {
        countryRepository = mock(CountryRepository.class);
        countryMapper = mock(CountryMapper.class);
        countryService = new CountryService(countryRepository, countryMapper);
    }

    @Test
    void findAllCountries_whenCalled_returnList() {
        Country country = Country.builder().id(1L).name("Country").build();
        when(countryRepository.findAll(any(Sort.class))).thenReturn(List.of(country));
        when(countryMapper.toCountryViewModelFromCountry(any())).thenReturn(new CountryVm(1L, "US", "USA", "USA", true, true, true, true, true));

        List<CountryVm> result = countryService.findAllCountries();

        assertThat(result).hasSize(1);
    }

    @Test
    void findById_whenNotFound_throwNotFoundException() {
        when(countryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.findById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_whenCodeDuplicated_throwDuplicatedException() {
        CountryPostVm postVm = CountryPostVm.builder().code2("US").name("USA").build();
        when(countryRepository.existsByCode2IgnoreCase("US")).thenReturn(true);

        assertThatThrownBy(() -> countryService.create(postVm))
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void create_whenNameDuplicated_throwDuplicatedException() {
        CountryPostVm postVm = CountryPostVm.builder().code2("US").name("USA").build();
        when(countryRepository.existsByCode2IgnoreCase("US")).thenReturn(false);
        when(countryRepository.existsByNameIgnoreCase("USA")).thenReturn(true);

        assertThatThrownBy(() -> countryService.create(postVm))
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void create_whenValid_saveCountry() {
        CountryPostVm postVm = CountryPostVm.builder().code2("US").name("USA").build();
        when(countryRepository.existsByCode2IgnoreCase("US")).thenReturn(false);
        when(countryRepository.existsByNameIgnoreCase("USA")).thenReturn(false);
        Country country = new Country();
        when(countryMapper.toCountryFromCountryPostViewModel(postVm)).thenReturn(country);
        when(countryRepository.save(any())).thenReturn(country);

        countryService.create(postVm);

        verify(countryRepository).save(any());
    }

    @Test
    void update_whenNotFound_throwNotFoundException() {
        CountryPostVm postVm = CountryPostVm.builder().build();
        when(countryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.update(postVm, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_whenNameDuplicated_throwDuplicatedException() {
        CountryPostVm postVm = CountryPostVm.builder().name("USA").build();
        when(countryRepository.findById(1L)).thenReturn(Optional.of(new Country()));
        when(countryRepository.existsByNameIgnoreCaseAndIdNot("USA", 1L)).thenReturn(true);

        assertThatThrownBy(() -> countryService.update(postVm, 1L))
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void update_whenCodeDuplicated_throwDuplicatedException() {
        CountryPostVm postVm = CountryPostVm.builder().name("USA").code2("US").build();
        when(countryRepository.findById(1L)).thenReturn(Optional.of(new Country()));
        when(countryRepository.existsByNameIgnoreCaseAndIdNot("USA", 1L)).thenReturn(false);
        when(countryRepository.existsByCode2IgnoreCaseAndIdNot("US", 1L)).thenReturn(true);

        assertThatThrownBy(() -> countryService.update(postVm, 1L))
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void update_whenValid_saveCountry() {
        CountryPostVm postVm = CountryPostVm.builder().name("USA").code2("US").build();
        Country country = new Country();
        when(countryRepository.findById(1L)).thenReturn(Optional.of(country));
        when(countryRepository.existsByNameIgnoreCaseAndIdNot("USA", 1L)).thenReturn(false);
        when(countryRepository.existsByCode2IgnoreCaseAndIdNot("US", 1L)).thenReturn(false);

        countryService.update(postVm, 1L);

        verify(countryRepository).save(country);
    }

    @Test
    void delete_whenNotFound_throwNotFoundException() {
        when(countryRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> countryService.delete(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_whenValid_deleteFromRepository() {
        when(countryRepository.existsById(1L)).thenReturn(true);

        countryService.delete(1L);

        verify(countryRepository).deleteById(1L);
    }

    @Test
    void getPageableCountries_whenCalled_returnCountryListGetVm() {
        Page<Country> countryPage = new PageImpl<>(List.of(new Country()), PageRequest.of(0, 10), 1);
        when(countryRepository.findAll(any(Pageable.class))).thenReturn(countryPage);

        CountryListGetVm result = countryService.getPageableCountries(0, 10);

        assertThat(result.totalElements()).isEqualTo(1);
    }
}
