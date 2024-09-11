package com.yas.promotion.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PromotionValidator.class)
public @interface PromotionConstraint {
    String message() default "Promotion is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
