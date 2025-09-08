package com.ia.common.utilities.helper;

import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * A utility class to measure the duration in milliseconds of an action.
 * It provides methods to execute an action and return the time taken to complete it.
 * It can handle both actions that return a result and those that do not.
 * <p>
 * Example usage:
 * <p>
 * DurationCounter counter = new DurationCounter();
 * Long duration = counter.count(() -> {
 * // Action to be measured
 * });
 * <p>
 * CounterResult<String> result = counter.count(() -> {
 * // Action that returns a result
 * return "result";
 * });
 * String actionResult = result.result();
 * Long actionDuration = result.duration();
 *
 * @author Martin Blaise Signe
 */
@Component
public class DurationCounter {

    /**
     * Measures the duration of an action that does not return a result.
     *
     * @param action The action to be executed.
     * @return The duration in milliseconds taken to execute the action.
     */
    public Long count(ActionRunner action) {
        final long start = System.currentTimeMillis();
        action.run();
        return System.currentTimeMillis() - start;
    }

    /**
     * Measures the duration of an action that returns a result.
     *
     * @param action The action to be executed.
     * @param <T>    The type of the result returned by the action.
     * @return A CounterResult object containing the result of the action and the duration in milliseconds taken to execute it.
     */
    public <T> CounterResult<T> count(Supplier<T> action) {
        final long start = System.currentTimeMillis();
        T result = action.get();
        return new CounterResult<>(result, System.currentTimeMillis() - start);
    }

    /*** A functional interface representing an action that does not return a result.
     */
    @FunctionalInterface
    public static interface ActionRunner {
        void run();
    }

    /**
     * A record to hold the result of an action and the duration taken to execute it.
     *
     * @param result   The result of the action.
     * @param duration The duration in milliseconds taken to execute the action.
     * @param <T>      The type of the result.
     */
    public record CounterResult<T>(T result, Long duration) {
    }
}
