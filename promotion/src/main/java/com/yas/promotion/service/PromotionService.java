package com.yas.promotion.service;

import com.yas.promotion.exception.BadRequestException;
import com.yas.promotion.exception.DuplicatedException;
import com.yas.promotion.exception.NotFoundException;
import com.yas.promotion.model.Promotion;
import com.yas.promotion.model.PromotionApply;
import com.yas.promotion.repository.PromotionRepository;
import com.yas.promotion.repository.PromotionUsageRepository;
import com.yas.promotion.utils.Constants;
import com.yas.promotion.viewmodel.PromotionDetailVm;
import com.yas.promotion.viewmodel.PromotionListVm;
import com.yas.promotion.viewmodel.PromotionPostVm;
import com.yas.promotion.viewmodel.PromotionPutVm;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionUsageRepository promotionUsageRepository;

    public PromotionDetailVm createPromotion(PromotionPostVm promotionPostVm) {
        validateIfPromotionExistedSlug(promotionPostVm.getSlug());
        validateIfPromotionEndDateIsBeforeStartDate(promotionPostVm.getStartDate(), promotionPostVm.getEndDate());

        Promotion promotion = Promotion.builder()
                .name(promotionPostVm.getName())
                .slug(promotionPostVm.getSlug())
                .description(promotionPostVm.getDescription())
                .couponCode(promotionPostVm.getCouponCode())
                .applyTo(promotionPostVm.getApplyTo())
                .usageType(promotionPostVm.getUsageType())
                .usageLimit(promotionPostVm.getUsageLimit())
                .discountType(promotionPostVm.getDiscountType())
                .discountPercentage(promotionPostVm.getDiscountPercentage())
                .discountAmount(promotionPostVm.getDiscountAmount())
                .isActive(promotionPostVm.isActive())
                .startDate(promotionPostVm.getStartDate())
                .endDate(promotionPostVm.getEndDate())
                .minimumOrderPurchaseAmount(promotionPostVm.getMinimumOrderPurchaseAmount())
                .build();

        List<PromotionApply> promotionApplies =
                PromotionPostVm.createPromotionApplies(promotionPostVm, promotion);
        promotion.setPromotionApplies(promotionApplies);

        return PromotionDetailVm.fromModel(promotionRepository.save(promotion));
    }

    public PromotionDetailVm updatePromotion(PromotionPutVm promotionPutVm) {
        Optional<Promotion> promotionOp = promotionRepository.findById(promotionPutVm.getId());

        if (promotionOp.isEmpty()) {
            throw new NotFoundException(Constants.ErrorCode.PROMOTION_NOT_FOUND_ERROR_MESSAGE, promotionPutVm.getId());
        }

        Promotion promotion = promotionOp.get();

        promotion.setApplyTo(promotionPutVm.getApplyTo());
        promotion.setName(promotionPutVm.getName());
        promotion.setDescription(promotionPutVm.getDescription());
        promotion.setCouponCode(promotionPutVm.getCouponCode());
        promotion.setUsageType(promotionPutVm.getUsageType());
        promotion.setUsageLimit(promotionPutVm.getUsageLimit());
        promotion.setSlug(promotionPutVm.getSlug());
        promotion.setDiscountType(promotionPutVm.getDiscountType());
        promotion.setDiscountPercentage(promotionPutVm.getDiscountPercentage());
        promotion.setDiscountAmount(promotionPutVm.getDiscountAmount());
        promotion.setIsActive(promotionPutVm.isActive());
        promotion.setStartDate(promotionPutVm.getStartDate());
        promotion.setEndDate(promotionPutVm.getEndDate());
        promotion.setMinimumOrderPurchaseAmount(promotionPutVm.getMinimumOrderPurchaseAmount());

        promotion.setPromotionApplies(PromotionPutVm.createPromotionApplies(promotionPutVm, promotion));

        promotion = promotionRepository.save(promotion);
        return PromotionDetailVm.fromModel(promotion);
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

    public void deletePromotion(Long id) {
        if (promotionUsageRepository.existsByPromotionId(id)) {
            throw new BadRequestException(Constants.ErrorCode.PROMOTION_IN_USE_ERROR_MESSAGE, id);
        }
        promotionRepository.deleteById(id);
    }
}
