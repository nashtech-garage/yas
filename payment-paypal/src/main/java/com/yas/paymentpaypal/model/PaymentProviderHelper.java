package com.yas.paymentpaypal.model;

public class PaymentProviderHelper {
    public static final String PAYPAL_PAYMENT_PROVIDER_ID = "PaypalPayment";

    private PaymentProviderHelper() {
        // Private constructor to prevent instantiation
    }
    public static PaymentProviderHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final PaymentProviderHelper INSTANCE = new PaymentProviderHelper();
    }

    public String getPaypalPaymentProviderId() {
        return PAYPAL_PAYMENT_PROVIDER_ID;
    }
}