package com.branch.framework.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * {@link SexValid} 的校验逻辑
 */
public class StatusValidator implements ConstraintValidator<SexValid, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.equals("男") || value.equals("女");
    }
}
