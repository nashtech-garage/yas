package com.yas.customer.service;

import com.yas.customer.viewmodel.CustomerVm;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public CustomerService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public List<CustomerVm> getCustomers() {
        return keycloak.realm(realm).users().list()
                .stream()
                .map(CustomerVm::fromUserRepresentation).toList();
    }
}
