package com.ia.common.utilities.helpher.normalizer;

import com.ia.common.utilities.helper.normalizer.DefaultObjectNormalizer;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultObjectNormalizerTest {

    private final DefaultObjectNormalizer normalizer = new DefaultObjectNormalizer();

    @Test
    void normalizeShouldReturnEmptyMap() {
        final var normalized = normalizer.normalize(null);
        assertThat(normalized).isEmpty();
    }

    @Test
    void normalizeShouldReturnFlatMapWhenSimpleMap() {
        final Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", "test");
        final var normalized = normalizer.normalize((Serializable) map);
        assertThat(normalized).containsExactlyInAnyOrderEntriesOf(map);
    }

    @Test
    void normalizeShouldReturnFlatMapWhenComplexMap() {
        final Map<String, Object> nested = new HashMap<>();
        nested.put("a", 1);
        nested.put("b", "test");
        final Map<String, Object> map = new HashMap<>();
        map.put("c", nested);
        final var normalized = normalizer.normalize((Serializable) map);
        assertThat(normalized).containsKey("c.a");
        assertThat(normalized).containsKey("c.b");
        assertThat(normalized.get("c.a")).isEqualTo(1);
        assertThat(normalized.get("c.b")).isEqualTo("test");
    }

    @Test
    void normalizeShouldHandleNestedEmptyMap() {
        final Map<String, Object> nested = new HashMap<>();
        final Map<String, Object> map = new HashMap<>();
        map.put("c", nested);
        final var normalized = normalizer.normalize((Serializable) map);
        assertThat(normalized).containsEntry("c", null);
    }

    @Test
    void normalizeShouldReturnEmptyMapWhenEmptySource() {
        final Map<String, Object> map = new HashMap<>();
        final var normalized = normalizer.normalize((Serializable) map);
        assertThat(normalized).isEmpty();
    }
}
