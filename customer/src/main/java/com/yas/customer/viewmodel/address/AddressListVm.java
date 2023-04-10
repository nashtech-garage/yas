package com.yas.customer.viewmodel.address;

import java.util.List;

public record AddressListVm(
        List<ActiveAddressVm> addressList,
        long totalElements,
        int totalPages
){ }
