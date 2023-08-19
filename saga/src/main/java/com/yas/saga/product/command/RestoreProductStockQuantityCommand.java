package com.yas.saga.product.command;

import io.eventuate.tram.commands.common.Command;
import lombok.Builder;

import java.util.List;

@Builder
public record RestoreProductStockQuantityCommand(List<ProductQuantityItem> productItems) implements Command {
}
