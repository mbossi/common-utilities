package com.ia.common.utilities.helpher;

import com.ia.common.utilities.helper.DurationCounter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class DurationCounterTest {

    private final DurationCounter durationCounter = new DurationCounter();

    @Test
    void testCounterForConsumerOperation() {
        final var count = durationCounter.count(this::consumerOperation);
        assertThat(count).isGreaterThanOrEqualTo(10);
    }

    @Test
    void testCounterForSupplierOperation() {
        final var result = durationCounter.count(this::supplierOperation);
        assertThat(result.result()).containsExactly("A", "B", "C");
        assertThat(result.duration()).isGreaterThanOrEqualTo(10);
    }

    @Test
    void testCounterForSupplierWithException() {
        try {
            final var result = durationCounter.count(this::supplierWithException);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(RuntimeException.class)
                    .hasMessage("Test exception");
        }
    }

    private void consumerOperation() {
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> supplierOperation() {
        try {
            TimeUnit.MILLISECONDS.sleep(10);
            return List.of("A", "B", "C");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> supplierWithException() {
        throw new RuntimeException("Test exception");
    }
}
