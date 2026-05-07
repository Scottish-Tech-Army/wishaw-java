package org.scottishtecharmy.wishaw_java.common;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DobValidator implements ConstraintValidator<ValidDob, LocalDate> {

    private static final LocalDate MIN_DOB = LocalDate.of(1900, 1, 1);
    private static final int MIN_AGE_YEARS = 3;
    private static final int MAX_AGE_YEARS = 150;

    @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext context) {
        // null is handled by @NotNull — allow null here so @ValidDob can be used on optional fields too
        if (dob == null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        LocalDate today = LocalDate.now();

        if (dob.isAfter(today)) {
            context.buildConstraintViolationWithTemplate("Date of birth cannot be in the future")
                    .addConstraintViolation();
            return false;
        }

        if (dob.isBefore(MIN_DOB)) {
            context.buildConstraintViolationWithTemplate("Date of birth cannot be before " + MIN_DOB)
                    .addConstraintViolation();
            return false;
        }

        if (dob.isAfter(today.minusYears(MIN_AGE_YEARS))) {
            context.buildConstraintViolationWithTemplate("Player must be at least " + MIN_AGE_YEARS + " years old")
                    .addConstraintViolation();
            return false;
        }

        if (dob.isBefore(today.minusYears(MAX_AGE_YEARS))) {
            context.buildConstraintViolationWithTemplate("Date of birth is unrealistically old")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}

