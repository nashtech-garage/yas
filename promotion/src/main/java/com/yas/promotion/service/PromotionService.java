package com.yas.promotion.service;

import com.yas.promotion.exception.BadRequestException;
import com.yas.promotion.exception.DuplicatedException;
import com.yas.promotion.model.Promotion;
import com.yas.promotion.repository.PromotionRepository;
import com.yas.promotion.utils.Constants;
import com.yas.promotion.viewmodel.PromotionDetailVm;
import com.yas.promotion.viewmodel.PromotionListVm;
import com.yas.promotion.viewmodel.PromotionPostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;

    public PromotionDetailVm createPromotion(PromotionPostVm promotionPostVm) {
        validateIfPromotionExistedSlug(promotionPostVm.slug());
        validateIfPromotionEndDateIsBeforeStartDate(promotionPostVm.startDate(), promotionPostVm.endDate());

        Promotion promotion = Promotion.builder()
                .name(promotionPostVm.name())
                .slug(promotionPostVm.slug())
                .description(promotionPostVm.description())
                .couponCode(promotionPostVm.couponCode())
                .applyTo(promotionPostVm.applyTo())
                .usageType(promotionPostVm.usageType())
                .usageLimit(promotionPostVm.usageLimit())
                .discountType(promotionPostVm.discountType())
                .discountPercentage(promotionPostVm.discountPercentage())
                .discountAmount(promotionPostVm.discountAmount())
                .isActive(promotionPostVm.isActive())
                .startDate(promotionPostVm.startDate())
                .endDate(promotionPostVm.endDate())
                .build();

        return PromotionDetailVm.fromModel(promotionRepository.save(promotion));
    }

    public PromotionListVm getPromotions(
        int pageNo,
        int pageSize,
        String promotionName,
        String couponCode,
        ZonedDateTime startDate,
        ZonedDateTime endDate
    ) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        Page<Promotion> promotionPage;

        promotionPage = promotionRepository.findPromotions(
            promotionName.trim(),
            couponCode.trim(),
            startDate,
            endDate,
            pageable
        );

        List<PromotionDetailVm> promotionDetailVmList = promotionPage
                .getContent()
                .stream()
                .map(PromotionDetailVm::fromModel)
                .collect(Collectors.toList());

        return PromotionListVm.builder()
                .promotionDetailVmList(promotionDetailVmList)
                .pageNo(promotionPage.getNumber())
                .pageSize(promotionPage.getSize())
                .totalElements(promotionPage.getTotalElements())
                .totalPages(promotionPage.getTotalPages())
                .build();
    }

    private void validateIfPromotionExistedSlug(String slug) {
        if (promotionRepository.findBySlugAndIsActiveTrue(slug).isPresent()) {
            throw new DuplicatedException(String.format(Constants.ErrorCode.SLUG_ALREADY_EXITED, slug));
        }
    }

    private void validateIfPromotionEndDateIsBeforeStartDate(ZonedDateTime startDate, ZonedDateTime endDate) {
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            throw new BadRequestException(String.format(Constants.ErrorCode.DATE_RANGE_INVALID));
        }
    }
}
