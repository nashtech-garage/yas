package com.yas.saga.order.command;

import io.eventuate.tram.commands.common.Command;
import lombok.Builder;

@Builder
public record UpdateCheckoutStatusCommand(
    String checkoutId,
    CheckoutStatusCommand checkoutStatus
) implements Command {}
