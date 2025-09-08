package com.ia.common.utilities.helper.normalizer;

import com.ia.common.utilities.helper.math.ObjectHelper;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/***
 * Interface for normalizing objects into a map representation.
 * It extends the Normalizer interface, specifying the input type T and output type Map<String, Object>.
 * It also provides a default method to extract differences between two objects of type T.
 * @author Martin Blaise Signe
 * @param <T> the type of object to be normalized
 */
public interface ObjectNormalizer<T> extends Normalizer<T, Map<String, Object>> {

    /**
     * Default method to extract differences between two objects of type T.
     * It normalizes both objects and compares their map representations.
     * The method returns a map of differences, where each entry contains the key and the corresponding ObjectDiffDetails.
     * @return a BiFunction that takes two objects of type T and returns a map of differences
     */
    default BiFunction<T, T, Map<String, ObjectDiffDetails>> differenceExtractor() {
        return (actual, expected) -> {
            final var actualMap = normalize(actual);
            final var expectedMap = normalize(expected);
            record Detail(String key, ObjectDiffDetails value) {
            }
            return expectedMap.keySet().stream()
                    .map(k -> {
                        final var actualValue = actualMap.get(k);
                        final var expectedValue = expectedMap.get(k);
                        return ObjectHelper.isEqual(actualValue, expectedValue) ? null : new Detail(k, new ObjectDiffDetails(actualValue, expectedValue));
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(Detail::key, Detail::value));

        };
    }

    /**
     * Default method to extract differences between two objects of type T, excluding specified keys.
     * It uses the differenceExtractor method and filters out entries with keys in the excludedKeys list.
     * @param excludedKeys a list of keys to be excluded from the difference extraction
     * @return a BiFunction that takes two objects of type T and returns a map of differences, excluding specified keys
     */
    default BiFunction<T,T, Map<String, ObjectDiffDetails>> differenceExtractor(List<String> excludedKeys) {
        return (left, right) -> this.differenceExtractor().apply(left, right).entrySet()
                .stream()
                .filter(e -> !excludedKeys.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    /**
     * Record to hold details of differences between two objects.
     * It contains the actual and expected values.
     * @param actual the actual value
     * @param expected the expected value
     */
    record ObjectDiffDetails(Object actual, Object expected) {
    }
}
