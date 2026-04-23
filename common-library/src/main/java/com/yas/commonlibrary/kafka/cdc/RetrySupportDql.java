package com.yas.commonlibrary.kafka.cdc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.SameIntervalTopicReuseStrategy;
import org.springframework.retry.annotation.Backoff;

/**
 * Custom annotation that extends Spring's {@link RetryableTopic} to
 * add retry and dead letter queue (DLQ) support for Kafka listeners.
 * Provides additional configuration for retry backoff, number of attempts,
 * topic creation, and exclusion of certain exceptions.
 */
@Documented
@RetryableTopic
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RetrySupportDql {

    @AliasFor(annotation = RetryableTopic.class, attribute = "backOff")
    BackOff kafkaBackoff() default @BackOff(value = 6000);

    Backoff backoff() default @Backoff(value = 6000);

    @AliasFor(annotation = RetryableTopic.class, attribute = "attempts")
    String attempts() default "4";

    @AliasFor(annotation = RetryableTopic.class, attribute = "autoCreateTopics")
    String autoCreateTopics() default "true";

    @AliasFor(annotation = RetryableTopic.class, attribute = "listenerContainerFactory")
    String listenerContainerFactory() default "";

    @AliasFor(annotation = RetryableTopic.class, attribute = "exclude")
    Class<? extends Throwable>[] exclude() default {};

    @AliasFor(annotation = RetryableTopic.class, attribute = "sameIntervalTopicReuseStrategy")
    SameIntervalTopicReuseStrategy sameIntervalTopicReuseStrategy() default SameIntervalTopicReuseStrategy.SINGLE_TOPIC;

}