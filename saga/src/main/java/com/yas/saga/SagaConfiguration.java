package com.yas.saga;

import io.eventuate.tram.spring.optimisticlocking.OptimisticLockingDecoratorConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(OptimisticLockingDecoratorConfiguration.class)
public class SagaConfiguration {
}
