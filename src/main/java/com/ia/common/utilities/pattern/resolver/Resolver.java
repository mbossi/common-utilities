package com.ia.common.utilities.pattern.resolver;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A functional interface representing a resolver that maps keys of type K to values of type V.
 * It provides a method to resolve a value based on a given key, returning an Optional<V>.
 * Additionally, it includes a default method to retrieve values from a Map using the key.
 *
 * @param <K> the type of keys used for resolution
 * @param <V> the type of values produced by the resolver
 */
@FunctionalInterface
public interface Resolver<K, V> {

    /**
     * Resolves a value based on the provided key.
     *
     * @param key the key to resolve the value for
     * @return an Optional containing the resolved value, or an empty Optional if not found
     */
    Optional<V> resolve(K key);

    /**
     * A default implementation of a value retriever that fetches values from a Map based on the provided key.
     * It returns an Optional containing the value if the key is present in the map, or an empty Optional otherwise.
     *
     * @return a BiFunction that takes a key and a map, returning an Optional value
     */
    default BiFunction<K, Map<K, V>, Optional<V>> valueRetriever() {
        return (key, map) -> Optional.ofNullable(key).map(map::get);
    }
}
