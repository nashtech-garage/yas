package com.yas.promotion.service;

import com.yas.promotion.exception.DuplicatedException;
import com.yas.promotion.model.Promotion;
import com.yas.promotion.repository.PromotionRepository;
import com.yas.promotion.utils.Constants;
import com.yas.promotion.viewmodel.PromotionDetailVm;
import com.yas.promotion.viewmodel.PromotionPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;

    public PromotionDetailVm createPromotion(PromotionPostVm promotionPostVm) {
        validateIfPromotionExistedSlug(promotionPostVm.slug());

        Promotion promotion = Promotion.builder()
                .name(promotionPostVm.name())
                .slug(promotionPostVm.slug())
                .description(promotionPostVm.description())
                .couponCode(promotionPostVm.couponCode())
                .value(promotionPostVm.value())
                .amount(promotionPostVm.amount())
                .isActive(true)
                .startDate(promotionPostVm.startDate())
                .endDate(promotionPostVm.endDate())
                .build();

        return PromotionDetailVm.fromModel(promotionRepository.saveAndFlush(promotion));
    }

    private void validateIfPromotionExistedSlug(String slug) {
        if (promotionRepository.findBySlugAndIsActiveTrue(slug).isPresent()) {
            throw new DuplicatedException(String.format(Constants.ERROR_CODE.SLUG_ALREADY_EXITED, slug));
        }
    }
}
