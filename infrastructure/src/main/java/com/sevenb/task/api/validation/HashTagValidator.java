package com.sevenb.task.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HashTagValidator implements ConstraintValidator<HashTag, String> {
    @Override
    public boolean isValid(final String hashTag,
                           final ConstraintValidatorContext constraintValidatorContext) {
        if (hashTag == null || hashTag.isBlank()) {
            return false;
        }

        return hashTag.trim().startsWith("#");
    }
}
