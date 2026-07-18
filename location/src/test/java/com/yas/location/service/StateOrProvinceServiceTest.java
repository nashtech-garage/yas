package com.yas.location.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.location.mapper.StateOrProvinceMapper;
import com.yas.location.model.Country;
import com.yas.location.model.StateOrProvince;
import com.yas.location.repository.CountryRepository;
import com.yas.location.repository.StateOrProvinceRepository;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceAndCountryGetNameVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceListGetVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvincePostVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class StateOrProvinceServiceTest {

    @Mock
    private StateOrProvinceRepository stateOrProvinceRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private StateOrProvinceMapper stateOrProvinceMapper;

    @InjectMocks
    private StateOrProvinceService stateOrProvinceService;

    private Country country;
    private StateOrProvince stateOrProvince;
    private StateOrProvincePostVm postVm;

    @BeforeEach
    void setUp() {
        country = Country.builder()
            .id(1L)
            .name("Vietnam")
            .build();

        stateOrProvince = StateOrProvince.builder()
            .id(1L)
            .name("Ho Chi Minh")
            .code("HCM")
            .type("City")
            .country(country)
            .build();

        postVm = new StateOrProvincePostVm("Ho Chi Minh", "HCM", "City", 1L);
    }

    @Test
    void createStateOrProvince_WhenValid_ShouldReturnStateOrProvince() {
        when(countryRepository.existsById(1L)).thenReturn(true);
        when(stateOrProvinceRepository.existsByNameIgnoreCaseAndCountryId("Ho Chi Minh", 1L)).thenReturn(false);
        when(countryRepository.getReferenceById(1L)).thenReturn(country);
        when(stateOrProvinceRepository.save(org.mockito.ArgumentMatchers.any(StateOrProvince.class))).thenReturn(stateOrProvince);

        StateOrProvince result = stateOrProvinceService.createStateOrProvince(postVm);

        assertThat(result.getName()).isEqualTo("Ho Chi Minh");
    }

    @Test
    void createStateOrProvince_WhenCountryNotFound_ShouldThrowNotFoundException() {
        when(countryRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> stateOrProvinceService.createStateOrProvince(postVm));
    }

    @Test
    void createStateOrProvince_WhenNameExists_ShouldThrowDuplicatedException() {
        when(countryRepository.existsById(1L)).thenReturn(true);
        when(stateOrProvinceRepository.existsByNameIgnoreCaseAndCountryId("Ho Chi Minh", 1L)).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> stateOrProvinceService.createStateOrProvince(postVm));
    }

    @Test
    void updateStateOrProvince_WhenValid_ShouldUpdate() {
        when(stateOrProvinceRepository.findById(1L)).thenReturn(Optional.of(stateOrProvince));
        when(stateOrProvinceRepository.existsByNameIgnoreCaseAndCountryIdAndIdNot("Ho Chi Minh", 1L, 1L)).thenReturn(false);

        stateOrProvinceService.updateStateOrProvince(postVm, 1L);

        verify(stateOrProvinceRepository).save(stateOrProvince);
    }

    @Test
    void delete_WhenValid_ShouldDelete() {
        when(stateOrProvinceRepository.existsById(1L)).thenReturn(true);

        stateOrProvinceService.delete(1L);

        verify(stateOrProvinceRepository).deleteById(1L);
    }

    @Test
    void findById_WhenValid_ShouldReturnVm() {
        when(stateOrProvinceRepository.findById(1L)).thenReturn(Optional.of(stateOrProvince));
        when(stateOrProvinceMapper.toStateOrProvinceViewModelFromStateOrProvince(stateOrProvince))
            .thenReturn(StateOrProvinceVm.fromModel(stateOrProvince));

        StateOrProvinceVm result = stateOrProvinceService.findById(1L);

        assertThat(result.name()).isEqualTo("Ho Chi Minh");
    }

    @Test
    void getStateOrProvinceAndCountryNames_ShouldReturnList() {
        when(stateOrProvinceRepository.findByIdIn(List.of(1L))).thenReturn(List.of(stateOrProvince));

        List<StateOrProvinceAndCountryGetNameVm> result = 
            stateOrProvinceService.getStateOrProvinceAndCountryNames(List.of(1L));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().stateOrProvinceName()).isEqualTo("Ho Chi Minh");
    }

    @Test
    void findAll_ShouldReturnList() {
        when(stateOrProvinceRepository.findAll()).thenReturn(List.of(stateOrProvince));
        when(stateOrProvinceMapper.toStateOrProvinceViewModelFromStateOrProvince(stateOrProvince))
            .thenReturn(StateOrProvinceVm.fromModel(stateOrProvince));

        List<StateOrProvinceVm> result = stateOrProvinceService.findAll();

        assertThat(result).hasSize(1);
    }

    @Test
    void getPageableStateOrProvinces_ShouldReturnList() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "name"));
        Page<StateOrProvince> page = new PageImpl<>(List.of(stateOrProvince), pageable, 1);
        when(stateOrProvinceRepository.getPageableStateOrProvincesByCountry(1L, pageable)).thenReturn(page);

        StateOrProvinceListGetVm result = stateOrProvinceService.getPageableStateOrProvinces(0, 10, 1L);

        assertThat(result.stateOrProvinceContent()).hasSize(1);
    }

    @Test
    void getAllByCountryId_ShouldReturnList() {
        when(stateOrProvinceRepository.findAllByCountryIdOrderByNameAsc(1L)).thenReturn(List.of(stateOrProvince));
        when(stateOrProvinceMapper.toStateOrProvinceViewModelFromStateOrProvince(stateOrProvince))
            .thenReturn(StateOrProvinceVm.fromModel(stateOrProvince));

        List<StateOrProvinceVm> result = stateOrProvinceService.getAllByCountryId(1L);

        assertThat(result).hasSize(1);
    }
}
