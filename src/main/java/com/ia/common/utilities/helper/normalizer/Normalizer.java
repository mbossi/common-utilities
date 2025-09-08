package com.ia.common.utilities.helper.normalizer;

/**
 * A functional interface for normalizing input of type I to output of type O.
 * It defines a single abstract method `normalize` that takes an input and returns the normalized output.
 * @author Martin Blaise Signe
 * @param <I> the type of the input to be normalized
 * @param <O> the type of the normalized output
 */
@FunctionalInterface
public interface Normalizer<I,O> {
    O normalize(I input);
}
