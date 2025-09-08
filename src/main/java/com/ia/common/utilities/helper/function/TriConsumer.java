package com.ia.common.utilities.helper.function;

/***
 * A functional interface that accepts three input arguments and returns no result.
 * This is the three-arity specialization of {@link java.util.function.Consumer}.
 * Unlike most other functional interfaces, {@code TriConsumer} is expected to operate via side-effects
 * and the accept method does not return a value.
 * @param <U> the type of the first argument to the operation
 * @param <T> the type of the second argument to the operation
 * @param <V> the type of the third argument to the operation
 * @see java.util.function.Consumer
 * @author Martin Blaise Signe
 */
@FunctionalInterface
public interface TriConsumer<U,T,V> {
    void accept(U u, T t, V v);
}
