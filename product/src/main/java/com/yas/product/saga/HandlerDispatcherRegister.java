package com.yas.product.saga;

import com.yas.product.saga.handler.ProductCommandHandler;
import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HandlerDispatcherRegister {
    @Bean
    public CommandDispatcher consumerCommandDispatcher(ProductCommandHandler target,
                                                       SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {

        return sagaCommandDispatcherFactory.make("productCommandHandlerDispatcher", target.commandHandlerDefinitions());
    }
}
