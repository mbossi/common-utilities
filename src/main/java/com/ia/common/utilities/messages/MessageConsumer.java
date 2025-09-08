package com.ia.common.utilities.messages;

import com.ia.common.utilities.helper.function.TriConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

/***
 * A functional interface that consumes a stream of messages of type T.
 * This is a specialization of {@link Consumer} that operates on a {@link Flux} of {@link Message} objects.
 * The interface provides a default method to handle message processing, including logging and error handling.
 * @param <T> the type of the payload contained in the Message
 * @see Consumer
 * @see Flux
 * @see Message
 * @author Martin Blaise Signe
 */
public interface MessageConsumer<T> extends Consumer<Flux<Message<T>>> {
    Logger log = LoggerFactory.getLogger(MessageConsumer.class);


    /*** Returns a TriConsumer that processes a Flux of Messages.
     * The TriConsumer takes three parameters:
     * - A Flux of Message<T> objects to be processed.
     * - A Consumer<T> to handle the extracted payloads.
     * - A Consumer<Throwable> to handle any errors that occur during processing.
     * The method logs the receipt of each message, extracts the payload, and logs the extracted payload.
     * It also logs when the processing is complete and handles any errors using the provided error handler.
     * @return a TriConsumer that processes messages with logging and error handling
     */
    default TriConsumer<Flux<Message<T>>, Consumer<T>, Consumer<Throwable>> messageHandler() {
        return (messages, messageHandler, errorHandler) -> messages
                .doOnNext(msg -> log.debug("Received message with payload: {}", msg.getPayload()))
                .map(Message::getPayload)
                .doOnNext(payload -> log.debug("Extracted payload: {}", payload))
                .doOnError(errorHandler)
                .doOnComplete(() -> log.debug("Completed processing messages"))
                .subscribe(messageHandler);
    }
}
