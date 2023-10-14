package com.yas.paymentpaypal.model;

public class PaymentProviderHelper {
    private static volatile PaymentProviderHelper uniqueInstance;
    public static final String PAYPAL_PAYMENT_PROVIDER_ID  = "PaypalPayment";

    private PaymentProviderHelper() {
        // Private constructor to prevent instantiation
    }
    public static PaymentProviderHelper getInstance() {
        if (uniqueInstance == null) {
            // Do the task too long before create instance ...
            // Block so other threads cannot come into while initialize
            synchronized (PaymentProviderHelper.class) {
                // Re-check again. Maybe another thread has initialized before
                if (uniqueInstance == null) {
                    uniqueInstance = new PaymentProviderHelper();
                }
            }
        }
        return uniqueInstance;
    }

    public String getPaypalPaymentProviderId(){
        return PAYPAL_PAYMENT_PROVIDER_ID;
    }
}