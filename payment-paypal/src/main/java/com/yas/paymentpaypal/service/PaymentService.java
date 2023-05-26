package com.yas.paymentpaypal.service;

import com.yas.paymentpaypal.config.ServiceUrlConfig;
import com.yas.paymentpaypal.exception.BadRequestException;
import com.yas.paymentpaypal.exception.Forbidden;
import com.yas.paymentpaypal.exception.SignInRequiredException;
import com.yas.paymentpaypal.viewmodel.CapturedPaymentVm;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class PaymentService {

    private final WebClient webClient;
    private final ServiceUrlConfig serviceUrlConfig;

    public PaymentService(WebClient webClient, ServiceUrlConfig serviceUrlConfig) {
        this.webClient = webClient;
        this.serviceUrlConfig = serviceUrlConfig;
    }

    public CapturedPaymentVm capturePaymentInfoToPaymentService(CapturedPaymentVm capturedPayment) {
        final URI url = UriComponentsBuilder
                .fromHttpUrl(serviceUrlConfig.payment())
                .path("/capture-payment")
                .buildAndExpand()
                .toUri();

        return webClient.post()
                .uri(url)
                .bodyValue(capturedPayment)
                .retrieve()
                .onStatus(
                        HttpStatus.UNAUTHORIZED::equals,
                        response -> response.bodyToMono(String.class).map(SignInRequiredException::new))
                .onStatus(
                        HttpStatus.FORBIDDEN::equals,
                        response -> response.bodyToMono(String.class).map(Forbidden::new))
                .onStatus(
                        HttpStatus.BAD_REQUEST::equals,
                        response -> response.bodyToMono(String.class).map(BadRequestException::new))
                .bodyToMono(CapturedPaymentVm.class)
                .block();
    }
}
