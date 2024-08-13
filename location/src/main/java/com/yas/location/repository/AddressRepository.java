package com.yas.location.repository;

import com.yas.location.model.Address;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByIdIn(List<Long> ids);
}
