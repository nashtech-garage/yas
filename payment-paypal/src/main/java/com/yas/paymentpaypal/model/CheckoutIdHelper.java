package com.yas.paymentpaypal.model;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CheckoutIdHelper {
    private static Map<String, String> orderIdToCheckoutIdMap = new ConcurrentHashMap<>();

    private CheckoutIdHelper() {
        // Private constructor to prevent instantiation
    }

    public static CheckoutIdHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final CheckoutIdHelper INSTANCE = new CheckoutIdHelper();
    }

    public static String getCheckoutIdByOrderId(String orderId) {
        return orderIdToCheckoutIdMap.get(orderId);
    }

    public static void setCheckoutId(String orderId, String checkoutId) {
        orderIdToCheckoutIdMap.put(orderId, checkoutId);
    }

    public static void removeCheckoutIdByOrderId(String orderId) {
        orderIdToCheckoutIdMap.remove(orderId);
    }
}