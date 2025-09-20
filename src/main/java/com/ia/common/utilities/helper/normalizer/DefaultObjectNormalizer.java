package com.ia.common.utilities.helper.normalizer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ia.common.utilities.helper.SerializerHelper;
import lombok.NonNull;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A default implementation of the ObjectNormalizer interface that normalizes Serializable objects into a flat Map structure.
 * It converts nested maps into a single-level map with dot-separated keys representing the hierarchy.
 * For example, a nested map like {"a": {"b": 1}} will be normalized to {"a.b": 1}.
 * This class uses the SerializerHelper to convert the input object into a Map before normalization.
 * If the input is null or cannot be converted, it returns an empty map.
 * <p>
 * Example usage:
 * <pre>
 *     DefaultObjectNormalizer normalizer = new DefaultObjectNormalizer();
 *     Map<String, Object> normalizedMap = normalizer.normalize(yourSerializableObject);
 * </pre>
 *
 * @see ObjectNormalizer
 * @see SerializerHelper
 * @author Martin Blaise Signe
 */
@SuppressWarnings({"unchecked"})
@Component
public class DefaultObjectNormalizer implements ObjectNormalizer<Serializable> {

    private static final String KEY_BUILDER_TEMPLATE = "%s.%s";
    private static final BiFunction<Object, String, String> KEY_FORMATTER = (key, parentField) -> KEY_BUILDER_TEMPLATE.formatted(parentField, key.toString());

    @Override
    public Map<String, Object> normalize(Serializable input) {
        final Function<Serializable, Map<String, Object>> deserializer = s -> SerializerHelper.convert(s, new TypeReference<Map<String, Object>>() {
        });

        return Optional.ofNullable(input)
                .map(deserializer)
                .map(this::dissect)
                .orElseGet(HashMap::new);
    }


    private Map<String, Object> dissect(@NonNull Map<String, Object> source) {

        final Supplier<Map<String, Object>> container = HashMap::new;
        final BiConsumer<Map<String, Object>, ObjectDetails> accumulator = (c, item) -> c.put(item.name(), item.value());
        final BiConsumer<Map<String, Object>, Map<String, Object>> combiner = Map::putAll;
        final Function<Map<String, Object>, Map<String, Object>> finisher = s -> getDetails(s, new ArrayList<>()).stream().collect(container, accumulator, combiner);

        return Optional.of(source)
                .filter(MapUtils::isNotEmpty)
                .map(finisher)
                .orElseGet(HashMap::new);
    }

    private List<ObjectDetails> getDetails(@NonNull Map<String, Object> map, @NonNull List<ObjectDetails> initialList) {
        for (final var e : map.entrySet()) {
            final var value = e.getValue();
            if (value instanceof Map<?, ?> m) {
                processMap(e.getKey(), (Map<String, Object>) m, initialList);
            } else {
                initialList.add(new ObjectDetails(e.getKey(), value));
            }
        }
        return initialList;
    }

    private void processMap(@NonNull String parentField, @NonNull Map<String, Object> map, @NonNull List<ObjectDetails> details) {
        if (MapUtils.isEmpty(map)) {
            details.add(ObjectDetails.empty(parentField));
        } else {
            for (final var e : map.entrySet()) {
                final var fieldName = String.format(KEY_BUILDER_TEMPLATE, parentField, e.getKey());
                final var value = e.getValue();
                if (value instanceof Map<?, ?> m) {
                    processMap(KEY_FORMATTER.apply(e.getKey(), parentField), (Map<String, Object>) m, details);
                } else {
                    details.add(new ObjectDetails(KEY_FORMATTER.apply(e.getKey(),parentField), value));
                }
            }
        }

    }

    record ObjectDetails(String name, Object value) {
        public static ObjectDetails empty(String fieldName) {
            return new ObjectDetails(fieldName, null);
        }
    }
}
