package com.yas.saga.payment.command;

import io.eventuate.tram.commands.common.Command;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreatePaymentCommand(BigDecimal totalPrice, PaymentMethodCommand paymentMethod) implements Command {
}
