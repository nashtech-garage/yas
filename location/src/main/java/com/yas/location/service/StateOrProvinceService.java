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

    public StateOrProvince create(StateOrProvincePostVm stateOrProvincePostVm) {
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


    public StateOrProvince update(StateOrProvincePostVm stateOrProvincePostVm, Long id) {
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

    public StateOrProvinceListGetVm getPageableCountries(int pageNo, int pageSize, Long countryId) {
       // List<StateOrProvinceVm> stateOrProvinceVms = new ArrayList<>();
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<StateOrProvince> stateOrProvincePage = stateOrProvinceRepository.getStateOrProvinceByCountry(countryId, pageable);
        List<StateOrProvince> stateOrProvinceList = stateOrProvincePage.getContent();
//        for (StateOrProvince stateOrProvince : stateOrProvinceList) {
//            stateOrProvinceVms.add(StateOrProvinceVm.fromModel(stateOrProvince));
//        }

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

