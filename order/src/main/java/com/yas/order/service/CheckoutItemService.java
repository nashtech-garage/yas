package com.yas.order.service;

import com.yas.order.model.CheckoutItem;
import com.yas.order.repository.CheckoutItemRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckoutItemService {
    private final CheckoutItemRepository checkoutItemRepository;

    public List<CheckoutItem> getAllByCheckoutId(String checkoutId) {
        List<CheckoutItem> checkoutItemList = checkoutItemRepository.findAllByCheckoutId(checkoutId);
        if (CollectionUtils.isEmpty(checkoutItemList)) {
            return List.of();
        }
        return checkoutItemList;
    }

    public void saveAll(List<CheckoutItem> items) {
        if (CollectionUtils.isEmpty(items)) {
            return;
        }
        checkoutItemRepository.saveAll(items);
    }

}
