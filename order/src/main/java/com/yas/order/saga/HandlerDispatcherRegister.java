package com.yas.order.saga;

import com.yas.order.saga.handler.OrderCommandHandler;
import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HandlerDispatcherRegister {
    @Bean
    public CommandDispatcher consumerCommandDispatcher(OrderCommandHandler target,
                                                       SagaCommandDispatcherFactory sagaCommandDispatcherFactory) {

        return sagaCommandDispatcherFactory.make("orderCommandHandlerDispatcher", target.commandHandlerDefinitions());
    }
}
