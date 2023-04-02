package com.yas.location.repository;

import com.yas.location.mapper.AddressResponseMapper;
import com.yas.location.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<AddressResponseMapper> findAllByIdIn(List<Long> ids);
}
