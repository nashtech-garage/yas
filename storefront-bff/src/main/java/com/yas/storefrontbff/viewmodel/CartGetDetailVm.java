package com.yas.storefrontbff.viewmodel;

import java.util.List;

public record CartGetDetailVm(Long id, String customerId, List<CartDetailVm> cartDetails) {

}
