package com.yas.order.viewmodel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompletedOrder {
    private String status;
    private String payId;

    public CompletedOrder(String status) {
        this.status = status;
    }
}