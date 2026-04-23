package com.yas.media.utils;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FileTypeValidator.class)
@Target({ElementType.FIELD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFileType {
    String message() default "Invalid file type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] allowedTypes();
}
