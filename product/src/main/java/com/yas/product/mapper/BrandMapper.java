package com.yas.product.mapper;


import java.time.LocalDateTime;
import com.yas.product.model.Brand;
import com.yas.product.viewModel.BrandDto;
import com.yas.product.viewModel.BrandPostDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BrandMapper {

  Brand toEntity(BrandPostDto dto);

  BrandDto toDto(Brand entity);

  void updateEntity(@MappingTarget Brand entity, BrandPostDto dto);
}
