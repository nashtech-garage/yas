package com.yas.location.viewmodel.stateorprovince;

import java.util.List;

public record StateOrProvinceListGetVm(
        List<StateOrProvinceVm> stateOrProvinceContent,
        int pageNo,
        int pageSize,
        int totalElements,
        int totalPages,
        boolean isLast
) {
}

