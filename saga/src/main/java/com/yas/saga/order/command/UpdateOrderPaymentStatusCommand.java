package com.yas.saga.order.command;

import io.eventuate.tram.commands.common.Command;
import lombok.Builder;

@Builder
public record UpdateOrderPaymentStatusCommand(
    Long orderId,
    Long paymentId,
    PaymentStatusCommand paymentStatus

) implements Command {}
