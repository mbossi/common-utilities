package com.ia.common.utilities.helper.function;

/***
 * A predicate (boolean-valued function) of two arguments.
 * This is the two-arity specialization of Predicate.
 * The functional method of this interface is test(Object, Object).
 * @author Martin Blaise Signe
 * @param <T> the type of the input to the predicate
 */
@FunctionalInterface
public interface BinaryPredicate<T> {
    boolean test(T t1, T t2);
}
