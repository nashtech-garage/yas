package com.yas.inventory.exception;

import com.yas.inventory.utils.MessagesUtils;
import com.yas.inventory.viewmodel.error.ErrorVm;
import com.yas.inventory.viewmodel.error.OutStockErrorVm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OutOfStockException extends RuntimeException {
    private String message;

    public OutOfStockException(String errorCode, List<OutStockErrorVm> errors) {
        this.message = errors.stream()
                .map(error -> MessagesUtils.getMessage(errorCode, error.productId(), error.quantity()))
                .collect(Collectors.joining(", "));
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
