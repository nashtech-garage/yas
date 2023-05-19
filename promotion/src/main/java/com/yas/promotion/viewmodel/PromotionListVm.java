package com.yas.promotion.viewmodel;

import lombok.Builder;

import java.util.List;

@Builder
public record PromotionListVm(
        List<PromotionDetailVm> promotionDetailVmList,
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages
) {

}
