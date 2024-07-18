package com.yas.paymentpaypal.config;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import com.yas.paymentpaypal.model.PaymentProviderHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Configuration
public class PaypalConfig {
    @Bean
    @RequestScope
    public PayPalHttpClient getPaypalClient(RestClient restClient, ServiceUrlConfig serviceUrlConfig) {
        final URI url = UriComponentsBuilder.fromHttpUrl(serviceUrlConfig.payment()).path("/payment-providers/{id}/additional-settings")
                .buildAndExpand(PaymentProviderHelper.PAYPAL_PAYMENT_PROVIDER_ID).toUri();

        // Make a request to the payment-service to retrieve additionalSettings
        ResponseEntity<String> response = restClient.get()
                .uri(url)
                .retrieve()
                .toEntity(String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String additionalSettings = response.getBody();
            Assert.notNull(additionalSettings, "The additionalSettings can not be null.");
            // Parse the additionalSettings field to extract clientId and clientSecret
            JsonObject settingsJson = JsonParser.parseString(additionalSettings).getAsJsonObject();
            String clientId = settingsJson.get("clientId").getAsString();
            String clientSecret = settingsJson.get("clientSecret").getAsString();
            String mode = settingsJson.get("mode").getAsString();
            if(mode.equals("sandbox"))
                // Create PayPalHttpClient with the retrieved clientId and clientSecret
                return new PayPalHttpClient(new PayPalEnvironment.Sandbox(clientId, clientSecret));
            return new PayPalHttpClient(new PayPalEnvironment.Live(clientId, clientSecret));
        } else {
            // Handle the case when the payment-service request fails
            throw new IllegalStateException("Failed to retrieve additionalSettings from payment-service");
        }
    }
}