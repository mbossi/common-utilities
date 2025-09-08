package com.ia.common.utilities.converter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A functional interface for converting an input of type I to an output of type O.
 * @author Martin Blaise Signe
 * @param <I> the input type
 * @param <O> the output type
 */
@FunctionalInterface
public interface Converter<I, O> {

    /**
     * Converts the given input to an output wrapped in an Optional.
     *
     * @param input the input to convert
     * @return an Optional containing the converted output, or an empty Optional if conversion is not possible
     */
    Optional<O> convert(I input);

    /**
     * Converts a list of inputs to a list of outputs, filtering out any null inputs and empty Optionals.
     *
     * @param input the list of inputs to convert
     * @return a list of converted outputs
     */
    default List<O> convert(List<I> input) {
        return input.stream().filter(Objects::nonNull).map(this::convert)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }
}
