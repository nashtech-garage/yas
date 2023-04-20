package com.yas.order.config;


import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PaypalConfig {

    @Bean
    public PayPalHttpClient getPaypalClient(
            @Value("${paypal.client.id}") String clientId,
            @Value("${paypal.client.secret}") String clientSecret) {
        return new PayPalHttpClient(new PayPalEnvironment.Sandbox(clientId, clientSecret));
    }

}