package com.yas.location.viewmodel.country;

import java.util.List;

public record CountryListGetVm(
        List<CountryVm> countryContent,
        int pageNo,
        int pageSize,
        int totalElements,
        int totalPages,
        boolean isLast
) {
}

