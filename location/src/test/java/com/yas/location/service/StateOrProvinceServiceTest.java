package com.yas.location.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class StateOrProvinceServiceTest {
    private StateOrProvinceRepository stateOrProvinceRepository;
    private CountryRepository countryRepository;
    private StateOrProvinceMapper stateOrProvinceMapper;
    private StateOrProvinceService stateOrProvinceService;

    @BeforeEach
    void setUp() {
        stateOrProvinceRepository = mock(StateOrProvinceRepository.class);
        countryRepository = mock(CountryRepository.class);
        stateOrProvinceMapper = mock(StateOrProvinceMapper.class);
        stateOrProvinceService = new StateOrProvinceService(stateOrProvinceRepository, countryRepository, stateOrProvinceMapper);
    }

    @Test
    void createStateOrProvince_whenCountryNotFound_throwNotFoundException() {
        StateOrProvincePostVm postVm = StateOrProvincePostVm.builder().countryId(1L).build();
        when(countryRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> stateOrProvinceService.createStateOrProvince(postVm))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void createStateOrProvince_whenNameDuplicated_throwDuplicatedException() {
        StateOrProvincePostVm postVm = StateOrProvincePostVm.builder().countryId(1L).name("State").build();
        when(countryRepository.existsById(1L)).thenReturn(true);
        when(stateOrProvinceRepository.existsByNameIgnoreCaseAndCountryId("State", 1L)).thenReturn(true);

        assertThatThrownBy(() -> stateOrProvinceService.createStateOrProvince(postVm))
                .isInstanceOf(DuplicatedException.class);
    }



    @Test
    void createStateOrProvince_whenValid_saveStateOrProvince() {
        StateOrProvincePostVm postVm = StateOrProvincePostVm.builder().countryId(1L).name("State").build();
        when(countryRepository.existsById(1L)).thenReturn(true);
        when(stateOrProvinceRepository.existsByNameIgnoreCaseAndCountryId("State", 1L)).thenReturn(false);
        when(countryRepository.getReferenceById(1L)).thenReturn(new Country());
        when(stateOrProvinceRepository.save(any())).thenReturn(new StateOrProvince());

        stateOrProvinceService.createStateOrProvince(postVm);

        verify(stateOrProvinceRepository).save(any());
    }

    @Test
    void updateStateOrProvince_whenNameDuplicated_throwDuplicatedException() {
        StateOrProvincePostVm postVm = StateOrProvincePostVm.builder().name("New Name").build();
        StateOrProvince stateOrProvince = new StateOrProvince();
        stateOrProvince.setCountry(Country.builder().id(1L).build());
        
        when(stateOrProvinceRepository.findById(1L)).thenReturn(Optional.of(stateOrProvince));
        when(stateOrProvinceRepository.existsByNameIgnoreCaseAndCountryIdAndIdNot("New Name", 1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> stateOrProvinceService.updateStateOrProvince(postVm, 1L))
                .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void updateStateOrProvince_whenValid_saveStateOrProvince() {
        StateOrProvincePostVm postVm = StateOrProvincePostVm.builder().name("New Name").build();
        StateOrProvince stateOrProvince = new StateOrProvince();
        stateOrProvince.setCountry(Country.builder().id(1L).build());
        
        when(stateOrProvinceRepository.findById(1L)).thenReturn(Optional.of(stateOrProvince));
        when(stateOrProvinceRepository.existsByNameIgnoreCaseAndCountryIdAndIdNot("New Name", 1L, 1L)).thenReturn(false);

        stateOrProvinceService.updateStateOrProvince(postVm, 1L);

        assertThat(stateOrProvince.getName()).isEqualTo("New Name");
        verify(stateOrProvinceRepository).save(stateOrProvince);
    }

    @Test
    void updateStateOrProvince_whenNotFound_throwNotFoundException() {
        StateOrProvincePostVm postVm = StateOrProvincePostVm.builder().build();
        when(stateOrProvinceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stateOrProvinceService.updateStateOrProvince(postVm, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_whenNotFound_throwNotFoundException() {
        when(stateOrProvinceRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> stateOrProvinceService.delete(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_whenValid_deleteById() {
        when(stateOrProvinceRepository.existsById(1L)).thenReturn(true);

        stateOrProvinceService.delete(1L);

        verify(stateOrProvinceRepository).deleteById(1L);
    }

    @Test
    void findById_whenValid_returnVm() {
        StateOrProvince stateOrProvince = new StateOrProvince();
        when(stateOrProvinceRepository.findById(1L)).thenReturn(Optional.of(stateOrProvince));
        when(stateOrProvinceMapper.toStateOrProvinceViewModelFromStateOrProvince(stateOrProvince)).thenReturn(new StateOrProvinceVm(1L, "State", "CODE", "TYPE", 1L));

        StateOrProvinceVm result = stateOrProvinceService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void findById_whenNotFound_throwNotFoundException() {
        when(stateOrProvinceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stateOrProvinceService.findById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getStateOrProvinceAndCountryNames_whenCalled_returnList() {
        StateOrProvince stateOrProvince = StateOrProvince.builder()
                .name("State")
                .country(Country.builder().name("Country").build())
                .build();
        when(stateOrProvinceRepository.findByIdIn(any())).thenReturn(List.of(stateOrProvince));

        List<StateOrProvinceAndCountryGetNameVm> result = stateOrProvinceService.getStateOrProvinceAndCountryNames(List.of(1L));

        assertThat(result).hasSize(1);
    }

    @Test
    void getPageableStateOrProvinces_whenCalled_returnStateOrProvinceListGetVm() {
        StateOrProvince stateOrProvince = StateOrProvince.builder().id(1L).country(Country.builder().id(1L).build()).build();
        Page<StateOrProvince> page = new PageImpl<>(List.of(stateOrProvince), PageRequest.of(0, 10), 1);
        when(stateOrProvinceRepository.getPageableStateOrProvincesByCountry(anyLong(), any(Pageable.class))).thenReturn(page);

        StateOrProvinceListGetVm result = stateOrProvinceService.getPageableStateOrProvinces(0, 10, 1L);

        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void getAllByCountryId_whenCalled_returnList() {
        StateOrProvince stateOrProvince = new StateOrProvince();
        when(stateOrProvinceRepository.findAllByCountryIdOrderByNameAsc(1L)).thenReturn(List.of(stateOrProvince));
        when(stateOrProvinceMapper.toStateOrProvinceViewModelFromStateOrProvince(any())).thenReturn(new StateOrProvinceVm(1L, "State", "CODE", "TYPE", 1L));

        List<StateOrProvinceVm> result = stateOrProvinceService.getAllByCountryId(1L);

        assertThat(result).hasSize(1);
    }
}
