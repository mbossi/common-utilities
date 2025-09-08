package com.ia.common.utilities.helper;

import com.google.common.collect.ImmutableMap;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A builder class for creating immutable maps with a fluent API.
 * It extends HashMap to allow easy conversion to a standard map if needed.
 * Null keys and values are ignored when adding entries.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author Martin Blaise Signe
 */
public class MapBuilder<K, V> extends HashMap<K, V> {

    private final transient Map<K, V> map;

    private MapBuilder() {
        this.map = new LinkedHashMap<>();
    }

    public static <I,O> MapBuilder<I, O> builder() {
        return new MapBuilder<>();
    }

    public MapBuilder<K, V> add(K key, V value) {
        if (key != null && value != null) {
            this.map.put(key, value);
        }
        return this;
    }

    public Map<K, V> build() {
        this.putAll(ImmutableMap.copyOf(map));
        return this;
    }

    public Properties toProperties() {
        final Properties props = new Properties();
        props.putAll(map);
        return props;
    }

    public static <K, V> Map<K, V> toMap(List<V> data, Function<V, K> keyMapper, BinaryOperator<V> mergeFunction) {
        return createMap(data, keyMapper, mergeFunction);
    }

    public static <K, V> Map<K, V> toMap(List<V> data, Function<V, K> keyMapper) {
        return createMap(data, keyMapper, (v1, v2) -> v2);
    }

    private static <K, V> Map<K, V> createMap(List<V> data, Function<V, K> keyMapper, BinaryOperator<V> mergeFunction) {
        return data.stream().collect(Collectors.toMap(keyMapper, Function.identity(), mergeFunction, HashMap::new));
    }
}
