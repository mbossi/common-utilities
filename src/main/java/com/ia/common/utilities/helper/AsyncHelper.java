package com.ia.common.utilities.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/***
 * A utility class to facilitate asynchronous task execution using CompletableFuture.
 * It provides methods to run tasks asynchronously with optional callbacks and exception handling.
 * The class leverages a provided Executor for task execution.
 * @author Martin Blaise Signe
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AsyncHelper {

    private final Executor executor;

    /**
     * Runs a task asynchronously using the provided executor.
     *
     * @param task the task to be executed asynchronously
     * @param <T>  the type of the result produced by the task
     * @return a CompletableFuture representing the pending result of the task
     */
    public <T> CompletableFuture<T> run(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, executor);
    }

    /**
     * Runs a task asynchronously with a callback and exception handler.
     *
     * @param task             the task to be executed asynchronously
     * @param callback         the callback to be executed upon successful completion of the task
     * @param exceptionHandler the handler to be executed if an exception occurs during task execution
     * @param <T>              the type of the result produced by the task
     * @return a CompletableFuture representing the pending result of the task
     */
    public <T> CompletableFuture<T> run(Supplier<T> task, Consumer<T> callback, Consumer<Exception> exceptionHandler) {
        return run(task).whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("Error occurred during async task execution", exception);
                if (exceptionHandler != null) {
                    exceptionHandler.accept((Exception) exception);
                }
            } else {
                if (callback != null) {
                    callback.accept(result);
                }
            }
        });
    }

    /**
     * Runs a task asynchronously with a callback.
     *
     * @param task     the task to be executed asynchronously
     * @param callback the callback to be executed upon successful completion of the task
     * @param <T>      the type of the result produced by the task
     * @return a CompletableFuture representing the pending result of the task
     */
    public <T> CompletableFuture<T> run(Supplier<T> task, Consumer<T> callback) {
        return run(task, callback, null);
    }

    /**
     * Runs a Runnable task asynchronously with a callback and exception handler.
     *
     * @param task             the Runnable task to be executed asynchronously
     * @param callback         the callback to be executed upon successful completion of the task
     * @param exceptionHandler the handler to be executed if an exception occurs during task execution
     */
    public void run(Runnable task, Consumer<Void> callback, Consumer<Throwable> exceptionHandler) {
        CompletableFuture.runAsync(task, executor).whenComplete((result, exception) -> {
            completionHandler(callback, exceptionHandler).accept(result, exception);
        });
    }

   /**
     * Runs a Runnable task asynchronously.
     *
     * @param task the Runnable task to be executed asynchronously
     */
    public void run(Runnable task) {
        run(task, null, null);
    }

    /**
     * Extracts the result from a CompletableFuture, handling exceptions and returning an Optional.
     *
     * @param future the CompletableFuture from which to extract the result
     * @param <T>    the type of the result produced by the CompletableFuture
     * @return an Optional containing the result if successful, or empty if an exception occurred
     */
    public static <T> Optional<T> get(CompletableFuture<T> future) {
        try {
            return Optional.ofNullable(future.get());
        } catch (Exception e) {
            log.error("Error occurred during the result extraction.", e);
            return Optional.empty();
        }
    }

    private <T> BiConsumer<T, Throwable> completionHandler(Consumer<T> resultHandler, Consumer<Throwable> errorHandler) {
        return (result, exception) -> {
            if (exception != null) {
                log.error("Error occurred during async task execution", exception);
                if (errorHandler != null) {
                    errorHandler.accept(exception);
                }
            } else {
                if (resultHandler != null) {
                    resultHandler.accept(result);
                }
            }
        };
    }
}
