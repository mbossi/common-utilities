package com.ia.common.utilities.helper;

import lombok.experimental.UtilityClass;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * A utility class that provides methods to introduce delays in the execution of actions.
 * It supports both {@link Runnable} and {@link Supplier} actions, allowing for flexible usage.
 * The delay can be specified in various time units using {@link TimeUnit}.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * // Delay a Runnable action by 2 seconds
 * DelayHelper.delay(() -> System.out.println("Action executed after delay"), TimeUnit.SECONDS, 2);
 *
 * // Delay a Supplier action by 500 milliseconds and get the result
 * String result = DelayHelper.delay(() -> "Result after delay", TimeUnit.MILLISECONDS, 500);
 * System.out.println(result);
 * }
 * </pre>
 * </p>
 * This class is thread-safe and can be used in concurrent environments.
 * It handles interruptions by re-interrupting the current thread.
 *
 * @author Martin Blaise Signe
 */
@UtilityClass
public class DelayHelper {

    /**
     * Introduces a delay before executing the given action.
     *
     * @param action the action to be executed after the delay
     * @param unit   the time unit of the delay
     * @param delay  the duration of the delay
     * @param <T>    the type of the result produced by the action
     * @return the result of the action
     */
    public <T> T delay(Supplier<T> action, TimeUnit unit, long delay) {
        delayConsumer().accept(unit, delay);
        return action.get();
    }

    /**
     * Introduces a delay before executing the given action.
     *
     * @param action the action to be executed after the delay
     * @param unit   the time unit of the delay
     * @param delay  the duration of the delay
     */
    public void delay(Runnable action, TimeUnit unit, long delay) {
        delayConsumer().accept(unit, delay);
        action.run();
    }

    /***
     * Introduces a delay in seconds before executing the given action.
     * @param action the action to be executed after the delay
     * @param delayInSeconds the duration of the delay in seconds
     * @param <T> the type of the result produced by the action
     * @return the result of the action
     */
    public <T> T delayInSeconds(Supplier<T> action, long delayInSeconds) {
        return delay(action, TimeUnit.SECONDS, delayInSeconds);
    }

    /***
     * Introduces a delay in seconds before executing the given action.
     * @param action the action to be executed after the delay
     * @param delayInSeconds the duration of the delay in seconds
     */
    public void delayInSeconds(Runnable action, long delayInSeconds) {
        delay(action, TimeUnit.SECONDS, delayInSeconds);
    }

    private BiConsumer<TimeUnit, Long> delayConsumer() {
        return (unit, duration) -> {
            try {
                unit.sleep(duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }
}
