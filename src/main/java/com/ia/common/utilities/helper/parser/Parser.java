package com.ia.common.utilities.helper.parser;

/**
 * Generic parsing specification
 * @author Martin Blaise Signe
 * @param <S> data source type
 * @param <O> data output type
 */
@FunctionalInterface
public interface Parser<S, O> {
    /**
     * Parse the input source to output objects
     *
     * @param input Source to parse
     * @return parsed result.
     */
    O parse(S input);
}
