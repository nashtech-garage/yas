package com.yas.customer.viewmodel.Address;

import java.util.List;

public record AddressListVm(
        List<ActiveAddressVm> addressList,
        long totalElements,
        int totalPages
){ }
