package com.branch.framework.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 性别校验注解，只允许男或女
 */
@Documented
@Constraint(validatedBy = StatusValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SexValid {

    String message() default "性别只能是男或女";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
