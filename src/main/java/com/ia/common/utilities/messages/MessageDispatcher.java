package com.ia.common.utilities.messages;

import com.ia.common.utilities.helper.AsyncHelper;
import com.ia.common.utilities.helper.function.TriConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.function.*;

/***
 * A generic interface for dispatching messages of type T.
 * It extends Supplier to provide a stream of messages as a Flux.
 * The interface includes methods for synchronous and asynchronous dispatching,
 * as well as utilities for emitting and sending messages using Reactor's Sinks.
 * It also defines default constants for timeout duration and event type key.
 * @author Martin Blaise Signe
 * @param <T> the type of the message payload
 */
public interface MessageDispatcher<T> extends Supplier<Flux<Message<T>>> {
    /***
     * Default timeout duration for message dispatching operations.
     * Set to 30 seconds.
     * @see Duration
     */
    Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30L);

    /***
     * Key used to identify the type of event in message headers.
     * @see MessageBuilder
     * @see Message
     */
    String EVENT_TYPE_KEY = "EventType";

    Logger log = LoggerFactory.getLogger(MessageDispatcher.class);

    /***
     * Dispatches a message with the given payload.
     * @param payload the payload of the message to be dispatched
     */
    void dispatch(T payload);

    /***
     * Dispatches synchronously a message with the given payload and executes a callback after dispatching.
     * @param payload the payload of the message to be dispatched
     * @param callback a Consumer to be executed after dispatching the message
     */
    default void dispatch(T payload, Consumer<T> callback) {
        this.dispatch(payload);
        callback.accept(payload);
    }

    /***
     * Dispatches synchronously a message with the given payload and executes a callback after dispatching.
     * @param payload the payload of the message to be dispatched
     * @param callback a Function to be executed after dispatching the message, returning a result of type U
     * @param <U> the type of the result produced by the callback
     * @return the result produced by the callback
     */
    default <U> U dispatch(T payload, Function<T, U> callback) {
        this.dispatch(payload);
        return callback.apply(payload);
    }

    /***
     * Dispatches asynchronously a message with the given payload using the provided AsyncHelper.
     * The dispatch operation is executed in a separate thread managed by the AsyncHelper.
     * @see AsyncHelper
     * @return a BiConsumer that takes an AsyncHelper and a payload, and performs the asynchronous dispatch
     */
    default BiConsumer<AsyncHelper, T> dispatchAsync() {
        return (asyncHelper, payload) -> {
            asyncHelper.run(() -> this.dispatch(payload));
        };
    }

    /***
     * Dispatches asynchronously a message with the given payload using the provided AsyncHelper.
     * The dispatch operation is executed in a separate thread managed by the AsyncHelper.
     * A callback is executed upon successful completion of the dispatch operation,
     * and an exception handler is invoked if an error occurs during dispatching.
     * @see AsyncHelper
     * @param callback a Consumer to be executed upon successful completion of the dispatch operation
     * @param exceptionHandler a Consumer to handle any exceptions that occur during dispatching
     * @return a BiConsumer that takes an AsyncHelper and a payload, and performs the asynchronous dispatch with callback and exception handling
     */
    default BiConsumer<AsyncHelper, T> dispatchAsync(Consumer<Void> callback, Consumer<Throwable> exceptionHandler) {
        return (asyncHelper, payload) -> {
            asyncHelper.run(() -> this.dispatch(payload), callback, exceptionHandler);
        };
    }

    /***
     * Dispatches asynchronously a message with the given payload using the provided AsyncHelper.
     * The dispatch operation is executed in a separate thread managed by the AsyncHelper.
     * A callback is executed upon successful completion of the dispatch operation.
     * @see AsyncHelper
     * @param callback a Consumer to be executed upon successful completion of the dispatch operation
     * @return a BiConsumer that takes an AsyncHelper and a payload, and performs the asynchronous dispatch with callback
     */
    default BiConsumer<AsyncHelper, T> dispatchAsync(Consumer<T> callback) {
        return (asyncHelper, payload) -> {
            this.dispatchAsync().accept(asyncHelper, payload);
            callback.accept(payload);
        };
    }

    /***
     * Provides a TriConsumer that emits messages to a Reactor Sinks.Many instance.
     * The TriConsumer takes a Sinks.Many<Message<T>>, a MessageBuilder<T>, and a Duration for retrying on failure.
     * It builds the message from the MessageBuilder, logs the emission, and emits the message to the sink,
     * using a busy looping strategy for handling emission failures with the specified retry duration.
     * @return a TriConsumer for emitting messages to a Sinks.Many instance
     * @see Sinks
     * @see MessageBuilder
     * @see Duration
     */
    default TriConsumer<Sinks.Many<Message<T>>, MessageBuilder<T>, Duration> emitter() {
        return (sink, builder, retryDuration) -> {
            Message<T> message = builder.build();
            log.info("Emitting message with header: {}", message.getHeaders());
            sink.emitNext(message, Sinks.EmitFailureHandler.busyLooping(retryDuration));
        };
    }

    /***
     * Provides a BiFunction that converts a Reactor Sinks.Many instance into a Flux stream of messages.
     * The BiFunction takes a Sinks.Many<Message<T>> and a Consumer<Throwable> for handling exceptions.
     * It returns a Flux<Message<T>> that emits messages from the sink, logging each message sent,
     * completion of the message stream, and handling errors using the provided exception handler.
     * @return a BiFunction for converting a Sinks.Many instance into a Flux stream of messages
     * @see Sinks
     * @see Flux
     */
    default BiFunction<Sinks.Many<Message<T>>, Consumer<Throwable>, Flux<Message<T>>> sender() {
        return (sink, exceptionHandler) -> sink.asFlux()
                .doOnNext(m -> log.info("Sending message with header: {}", m.getHeaders()))
                .doOnComplete(() -> log.info("Message stream completed"))
                .doOnError(exceptionHandler);

    }
}
