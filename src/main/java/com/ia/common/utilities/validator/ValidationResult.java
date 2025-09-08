package com.ia.common.utilities.validator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic class representing the result of a validation process.
 * It contains the original request, a list of validation errors, and a validity flag.
 *
 * @param <T> the type of the request being validated
 * @author Martin Blaise Signe
 */
@AllArgsConstructor
@Getter
@Builder
public class ValidationResult<T> {
    private T request;
    @Builder.Default
    private List<String> errors = new ArrayList<>();
    Boolean isValid;
}
