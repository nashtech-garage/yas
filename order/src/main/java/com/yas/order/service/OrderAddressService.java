package com.yas.order.service;

import static com.yas.order.utils.Constants.ErrorCode.ORDER_ADDRESS_NOT_FOUND;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.order.model.OrderAddress;
import com.yas.order.repository.OrderAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderAddressService {

    private final OrderAddressRepository orderAddressRepository;

    public OrderAddress findOrderAddressById(Long id) {
        return this.orderAddressRepository.findById(id)
            .orElseThrow(() -> new NotFoundException(ORDER_ADDRESS_NOT_FOUND, id));
    }
}

