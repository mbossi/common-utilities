package com.ia.common.utilities.helpher;

import com.ia.common.utilities.helper.MapBuilder;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MapBuilderTest {

    @Test
    void testFill() {
        final Map<String, Object> map = MapBuilder.<String, Object>builder()
                .add("replyCode", "A")
                .add("tradeReference", "TPW-6666")
                .add("contractNumber", "356710")
                .add("errorDescription", "Invalid securityType")
                .build();
        assertThat(map).hasSize(4);
    }

    @Test
    void testFillWithNullKey() {
        final Map<String, Object> map = MapBuilder.<String, Object>builder()
                .add("replyCode", "R")
                .add("tradeReference", "TPW-6666")
                .add("contractNumber", "356710")
                .add(null, "Invalid securityType")
                .build();
        assertThat(map).hasSize(3);
        assertThat(map.values().stream().filter(v -> v.toString().contains("securityType")).findFirst()).isNotPresent();
        assertThat(map.values().stream().filter(v -> v.toString().contains("TPW-6666")).findFirst()).isPresent();
    }

    @Test
    void testFillWithNullValue() {
        final Map<String, Object> map = MapBuilder.<String, Object>builder()
                .add("replyCode", "R")
                .add("tradeReference", "TPW-6666")
                .add("contractNumber", null)
                .add(null, "Invalid securityType")
                .build();
        assertThat(map).hasSize(2);
        assertThat(map.values().stream().filter(v -> v.toString().contains("securityType")).findFirst()).isNotPresent();
        assertThat(map.values().stream().filter(v -> v.toString().contains("TPW-6666")).findFirst()).isPresent();
        assertThat(map).doesNotContainKey("contractNumber");
    }
}
