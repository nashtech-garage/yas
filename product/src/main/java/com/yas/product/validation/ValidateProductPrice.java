package com.yas.product.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;




@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = PriceValidator.class)
@Documented
public @interface ValidateProductPrice {
    String message() default "Price must greater than 0";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

