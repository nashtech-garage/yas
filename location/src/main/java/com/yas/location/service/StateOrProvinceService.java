package com.yas.location.service;

import com.yas.location.exception.DuplicatedException;
import com.yas.location.exception.NotFoundException;
import com.yas.location.model.StateOrProvince;
import com.yas.location.model.Country;
import com.yas.location.utils.Constants;
import com.yas.location.repository.StateOrProvinceRepository;
import com.yas.location.repository.CountryRepository;
import com.yas.location.viewmodel.stateorprovince.StateOrProvincePostVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceListGetVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceVm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StateOrProvinceService {
    private final StateOrProvinceRepository stateOrProvinceRepository;

    private final CountryRepository countryRepository;

    public StateOrProvinceService(StateOrProvinceRepository stateOrProvinceRepository, CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
        this.stateOrProvinceRepository = stateOrProvinceRepository;
    }

    /**
     * handle business and create state or province
     *
     * @param stateOrProvincePostVm  The state or province post Dto
     *
     * @return StateOrProvince
     */
    public StateOrProvince createStateOrProvince(StateOrProvincePostVm stateOrProvincePostVm) {
        if (stateOrProvinceRepository.existsByName(stateOrProvincePostVm.name())) {
            throw new DuplicatedException(Constants.ERROR_CODE.NAME_ALREADY_EXITED, stateOrProvincePostVm.name());
        }
        Country country = countryRepository.findById(stateOrProvincePostVm.countryId()).orElseThrow(()
                -> new NotFoundException(Constants.ERROR_CODE.COUNTRY_NOT_FOUND, stateOrProvincePostVm.countryId()));

        StateOrProvince stateOrProvince = StateOrProvince.builder()
                .name(stateOrProvincePostVm.name())
                .code(stateOrProvincePostVm.code())
                .code(stateOrProvincePostVm.type())
                .country(country)
                .build();

        return stateOrProvinceRepository.save(stateOrProvince);
    }

    /**
     * Handle business and update state or province
     *
     * @param stateOrProvincePostVm   The state or province post Dto
     * @param  id                     The id of stateOrProvince need to update
     *
     * @return StateOrProvince
     */
    public StateOrProvince updateStateOrProvince(StateOrProvincePostVm stateOrProvincePostVm, Long id) {
        StateOrProvince stateOrProvince = stateOrProvinceRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.ERROR_CODE.STATE_OR_PROVINCE_NOT_FOUND, id));

        //For the updating case we we don't need to check for the state or province being updated
        if (stateOrProvinceRepository.existsByNameNotUpdatingStateOrProvince(stateOrProvincePostVm.name(), id)) {
            throw new DuplicatedException(Constants.ERROR_CODE.NAME_ALREADY_EXITED, stateOrProvincePostVm.name());
        }

        stateOrProvince.setName(stateOrProvincePostVm.name());
        stateOrProvince.setCode(stateOrProvincePostVm.code());
        stateOrProvince.setType(stateOrProvincePostVm.type());

        return stateOrProvinceRepository.save(stateOrProvince);
    }

    /**
     * Handle business and paging list of state or provinces
     *
     * @param pageNo     The number of page
     * @param pageSize   The number of row on every page
     * @param countryId  The country Id which  state or province belong
     *
     * @return StateOrProvince
     */
    public StateOrProvinceListGetVm getPageableStateOrProvinces(int pageNo, int pageSize, Long countryId) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<StateOrProvince> stateOrProvincePage = stateOrProvinceRepository.getStateOrProvinceByCountry(countryId, pageable);
        List<StateOrProvince> stateOrProvinceList = stateOrProvincePage.getContent();

        List<StateOrProvinceVm> stateOrProvinceVms = stateOrProvinceList.stream()
                .map(StateOrProvinceVm::fromModel)
                .toList();

        return new StateOrProvinceListGetVm(
                stateOrProvinceVms,
                stateOrProvincePage.getNumber(),
                stateOrProvincePage.getSize(),
                (int) stateOrProvincePage.getTotalElements(),
                stateOrProvincePage.getTotalPages(),
                stateOrProvincePage.isLast()
        );
    }
}
