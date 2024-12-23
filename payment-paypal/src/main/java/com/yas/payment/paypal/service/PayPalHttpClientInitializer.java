package com.yas.payment.paypal.service;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import com.paypal.core.PayPalEnvironment;
import com.paypal.core.PayPalHttpClient;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class PayPalHttpClientInitializer {

    public PayPalHttpClient createPaypalClient(String additionalSettings) {
        Assert.notNull(additionalSettings, "The additionalSettings can not be null.");
        // Parse the additionalSettings field to extract clientId and clientSecret
        JsonObject settingsJson = JsonParser.parseString(additionalSettings).getAsJsonObject();
        String clientId = settingsJson.get("clientId").getAsString();
        String clientSecret = settingsJson.get("clientSecret").getAsString();
        String mode = settingsJson.get("mode").getAsString();
        if (mode.equals("sandbox")) {
            // Create PayPalHttpClient with the retrieved clientId and clientSecret
            return new PayPalHttpClient(new PayPalEnvironment.Sandbox(clientId, clientSecret));
        }
        return new PayPalHttpClient(new PayPalEnvironment.Live(clientId, clientSecret));
    }
}
