package com.ia.common.utilities.helper.function;

/***
 * Represents a function that accepts three arguments and produces a result.
 * This is a functional interface whose functional method is {@link #apply(Object, Object, Object)}.
 *
 * @param <U> the type of the first argument to the function
 * @param <T> the type of the second argument to the function
 * @param <V> the type of the third argument to the function
 * @param <R> the type of the result of the function
 * @see java.util.function.Function
 * @author Martin Blaise Signe
 */
@FunctionalInterface
public interface TriFunction<U,T,V,R> {
    R apply(U u, T t, V v);
}
