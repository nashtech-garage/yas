package com.yas.order.service;

import com.yas.order.model.OrderItem;
import com.yas.order.repository.OrderItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public List<OrderItem> saveAll(List<OrderItem> items) {
        if (CollectionUtils.isEmpty(items)) {
            return List.of();
        }
        return orderItemRepository.saveAll(items);
    }

    public void findAllByOrderId(Long orderId) {
        orderItemRepository.findAllByOrderId(orderId);
    }

}
