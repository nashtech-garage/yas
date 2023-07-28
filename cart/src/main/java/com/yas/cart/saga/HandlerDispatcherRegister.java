package com.yas.cart.saga;

import com.yas.cart.saga.handler.CartItemCommandHandler;
import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HandlerDispatcherRegister {
    @Bean
    public CommandDispatcher consumerCommandDispatcher(CartItemCommandHandler target,
                                                       SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {

        return sagaCommandDispatcherFactory.make("cartItemCommandDispatcher", target.commandHandlerDefinitions());
    }
}
