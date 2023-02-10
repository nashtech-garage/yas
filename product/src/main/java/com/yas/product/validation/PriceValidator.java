package com.yas.product.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PriceValidator implements ConstraintValidator<ValidateProductPrice, Double> {

    @Override
    public void initialize(ValidateProductPrice constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Double productPrice, ConstraintValidatorContext constraintValidatorContext) {
        return productPrice>=0;
    }
}
