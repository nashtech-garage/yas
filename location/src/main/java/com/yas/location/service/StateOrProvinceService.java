package com.yas.location.service;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.location.mapper.StateOrProvinceMapper;
import com.yas.location.model.StateOrProvince;
import com.yas.location.repository.CountryRepository;
import com.yas.location.repository.StateOrProvinceRepository;
import com.yas.location.utils.Constants;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceAndCountryGetNameVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceListGetVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvincePostVm;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceVm;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StateOrProvinceService {

    private final StateOrProvinceRepository stateOrProvinceRepository;
    private final CountryRepository countryRepository;

    private final StateOrProvinceMapper stateOrProvinceMapper;

    public StateOrProvinceService(StateOrProvinceRepository stateOrProvinceRepository,
                                  CountryRepository countryRepository, StateOrProvinceMapper stateOrProvinceMapper) {
        this.stateOrProvinceRepository = stateOrProvinceRepository;
        this.countryRepository = countryRepository;
        this.stateOrProvinceMapper = stateOrProvinceMapper;
    }

    /**
     * handle business and create state or province.
     *
     * @param stateOrProvincePostVm The state or province post Dto
     * @return StateOrProvince
     */
    @Transactional
    public StateOrProvince createStateOrProvince(final StateOrProvincePostVm stateOrProvincePostVm) {
        final Long countryId = stateOrProvincePostVm.countryId();
        final boolean isCountryExisted = countryRepository.existsById(countryId);
        if (!isCountryExisted) {
            throw new NotFoundException(Constants.ErrorCode.COUNTRY_NOT_FOUND, countryId);
        }

        if (stateOrProvinceRepository.existsByNameIgnoreCaseAndCountryId(stateOrProvincePostVm.name(), countryId)) {
            throw new DuplicatedException(Constants.ErrorCode.NAME_ALREADY_EXITED,
                stateOrProvincePostVm.name());
        }

        final StateOrProvince stateOrProvince = StateOrProvince.builder()
            .name(stateOrProvincePostVm.name())
            .code(stateOrProvincePostVm.code())
            .type(stateOrProvincePostVm.type())
            .country(countryRepository.getReferenceById(countryId))
            .build();

        return stateOrProvinceRepository.save(stateOrProvince);
    }

    /**
     * Handle business and update state or province.
     *
     * @param stateOrProvincePostVm The state or province post Dto
     * @param id                    The id of stateOrProvince need to update
     */
    @Transactional
    public void updateStateOrProvince(final StateOrProvincePostVm stateOrProvincePostVm,
                                      final Long id) {
        final StateOrProvince stateOrProvince = stateOrProvinceRepository
            .findById(id)
            .orElseThrow(
                () -> new NotFoundException(Constants.ErrorCode.STATE_OR_PROVINCE_NOT_FOUND, id));

        //For the updating case we don't need to check for the state or province being updated
        if (stateOrProvinceRepository.existsByNameIgnoreCaseAndCountryIdAndIdNot(
            stateOrProvincePostVm.name(), stateOrProvince.getCountry().getId(), id)) {
            throw new DuplicatedException(Constants.ErrorCode.NAME_ALREADY_EXITED,
                stateOrProvincePostVm.name());
        }

        stateOrProvince.setName(stateOrProvincePostVm.name());
        stateOrProvince.setCode(stateOrProvincePostVm.code());
        stateOrProvince.setType(stateOrProvincePostVm.type());

        stateOrProvinceRepository.save(stateOrProvince);
    }

    @Transactional
    public void delete(final Long id) {
        final boolean isStateOrProvinceExisted = stateOrProvinceRepository.existsById(id);
        if (!isStateOrProvinceExisted) {
            throw new NotFoundException(Constants.ErrorCode.STATE_OR_PROVINCE_NOT_FOUND, id);
        }
        stateOrProvinceRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public StateOrProvinceVm findById(final Long id) {
        final StateOrProvince stateOrProvince = stateOrProvinceRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(Constants.ErrorCode.STATE_OR_PROVINCE_NOT_FOUND, id));
        return stateOrProvinceMapper.toStateOrProvinceViewModelFromStateOrProvince(stateOrProvince);
    }

    @Transactional(readOnly = true)
    public List<StateOrProvinceAndCountryGetNameVm> getStateOrProvinceAndCountryNames(
        final List<Long> stateOrProvinceIds) {
        List<StateOrProvince> stateOrProvinces = stateOrProvinceRepository.findByIdIn(stateOrProvinceIds);

        return stateOrProvinces.stream()
            .map(StateOrProvinceAndCountryGetNameVm::fromModel)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<StateOrProvinceVm> findAll() {
        return stateOrProvinceRepository
            .findAll()
            .stream()
            .map(stateOrProvinceMapper::toStateOrProvinceViewModelFromStateOrProvince)
            .toList();
    }

    /**
     * Handle business and paging list of state or provinces.
     *
     * @param pageNo    The number of page
     * @param pageSize  The number of row on every page
     * @param countryId The country Id which  state or province belong
     * @return StateOrProvince
     */
    @Transactional(readOnly = true)
    public StateOrProvinceListGetVm getPageableStateOrProvinces(int pageNo, int pageSize,
                                                                Long countryId) {
        final Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, "name"));
        final Page<StateOrProvince> stateOrProvincePage =
            stateOrProvinceRepository.getPageableStateOrProvincesByCountry(
                countryId, pageable);
        final List<StateOrProvince> stateOrProvinceList = stateOrProvincePage.getContent();

        final List<StateOrProvinceVm> stateOrProvinceVms = stateOrProvinceList.stream()
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

    public List<StateOrProvinceVm> getAllByCountryId(Long countryId) {
        return stateOrProvinceRepository.findAllByCountryIdOrderByNameAsc(countryId).stream()
            .map(stateOrProvinceMapper::toStateOrProvinceViewModelFromStateOrProvince)
            .toList();
    }
}
