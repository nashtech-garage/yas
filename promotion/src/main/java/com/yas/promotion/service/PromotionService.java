package com.yas.promotion.service;

import com.yas.promotion.exception.DuplicatedException;
import com.yas.promotion.model.Promotion;
import com.yas.promotion.repository.PromotionRepository;
import com.yas.promotion.utils.Constants;
import com.yas.promotion.viewmodel.PromotionPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;

    public Promotion create(PromotionPostVm promotionPostVm){
        validateExistedName(promotionPostVm.name(), null);

        Promotion promotion = Promotion.builder()
                .name(promotionPostVm.name())
                .description(promotionPostVm.description())
                .couponCode(promotionPostVm.couponCode())
                .value(promotionPostVm.value())
                .amount(promotionPostVm.amount())
                .isActive(promotionPostVm.isActive())
                .startDate(promotionPostVm.startDate())
                .endDate(promotionPostVm.endDate())
                .build();

        return promotionRepository.saveAndFlush(promotion);
    }

    private void validateExistedName(String name, Long id){
        if(checkExistedName(name, id)){
            throw new DuplicatedException(Constants.ERROR_CODE.NAME_ALREADY_EXITED, name);
        }
    }

    private boolean checkExistedName(String name, Long id){
        return promotionRepository.findExistedName(name, id) != null;
    }
}
