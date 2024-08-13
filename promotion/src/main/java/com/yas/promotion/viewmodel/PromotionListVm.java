package com.yas.promotion.viewmodel;

import java.util.List;
import lombok.Builder;

@Builder
public record PromotionListVm(
        List<PromotionDetailVm> promotionDetailVmList,
        int pageNo,
        int pageSize,
        long totalElements,
        int totalPages
) {

}
