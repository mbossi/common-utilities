package com.ia.common.utilities.validator;

import jakarta.validation.ConstraintViolation;

import java.util.List;
import java.util.function.BiFunction;

/**
 * A functional interface for validating requests of type I.
 * It provides a method to validate the request and return a ValidationResult.
 * The interface also includes default methods to build a ValidationResult
 * and extract error messages from constraint violations.
 *
 * @param <I> the type of the request to be validated
 * @author Martin Blaise Signe
 */
@FunctionalInterface
public interface Validator<I> {

    String INVALID_CONSTRAINT_VALUE_TEMPLATE = "%s %s.";
    String VALID_CONSTRAINT_VALUE_TEMPLATE = "%s '%s' %s.";

    /**
     * Validates the given request and returns a ValidationResult.
     *
     * @param request the request to be validated
     * @return a ValidationResult containing the validation outcome
     */
    ValidationResult<I> validate(I request);

    /***
     * Builds a ValidationResult object with the given result and errors.
     * @param result the validated request
     * @param errors the list of error messages
     * @return a ValidationResult containing the request, errors, and validity status
     */
    default ValidationResult<I> build(I result, List<String> errors) {
        return ValidationResult.<I>builder()
                .request(result)
                .errors(errors)
                .isValid(errors.isEmpty())
                .build();
    }

    /***
     * Provides a default error message extractor that uses the Jakarta Validator
     * to validate the request and extract error messages from constraint violations.
     * @return a BiFunction that takes a Validator and a request, and returns a list of error messages
     */
    default BiFunction<jakarta.validation.Validator, I, List<String>> errorMsgExtractor() {
        return (validator, request) -> validator.validate(request).stream()
                .map(this::getErrorMessage)
                .toList();
    }

    private String getErrorMessage(ConstraintViolation<I> constraint) {
        if (constraint.getInvalidValue() == null) {
            return INVALID_CONSTRAINT_VALUE_TEMPLATE.formatted(constraint.getPropertyPath().toString(), constraint.getMessage());
        }
        return VALID_CONSTRAINT_VALUE_TEMPLATE.formatted(constraint.getPropertyPath().toString(), constraint.getInvalidValue(), constraint.getMessage());
    }
}
