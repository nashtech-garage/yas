package com.yas.location.mapper;

import com.yas.location.model.StateOrProvince;
import com.yas.location.viewmodel.stateorprovince.StateOrProvinceVm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StateOrProvinceMapper {

  @Mapping(target = "countryId", source = "country.id")
  StateOrProvinceVm toStateOrProvinceViewModelFromStateOrProvince(StateOrProvince stateOrProvince);
}
