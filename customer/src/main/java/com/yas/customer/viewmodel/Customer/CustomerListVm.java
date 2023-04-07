package com.yas.customer.viewmodel.Customer;

import java.util.List;

public record CustomerListVm (int totalUser,List<CustomerAdminVm> customers, int totalPage ) {
    
}
