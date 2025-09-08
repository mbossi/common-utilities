package com.ia.common.utilities.helper.function;

/***
 * Represents a predicate (boolean-valued function) of two arguments.
 * This is a functional interface whose functional method is {@link #test(Object, Object)}.
 *
 * @param <T> the type of the first argument to the predicate
 * @param <U> the type of the second argument to the predicate
 * @see java.util.function.Predicate
 * @author Martin Blaise Signe
 */
@FunctionalInterface
public interface BiPredicate<T,U> {
    boolean test(T t, U u);
}
