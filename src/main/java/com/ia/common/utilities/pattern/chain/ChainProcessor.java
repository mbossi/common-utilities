package com.ia.common.utilities.pattern.chain;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 * A generic interface for processing requests in a chain of responsibility pattern.
 * Each processor can handle a request and optionally pass it to the next processor in the chain.
 *
 * @param <T> the type of the request
 * @param <R> the type of the response
 * @author Martin Blaise Signe
 */
public interface ChainProcessor<T, R> {

    /**
     * Processes the given request and returns a response.
     *
     * @param request the request to be processed
     * @return the response after processing the request
     */
    R process(ChainProcessorRequest<T, R> request);

    /**
     * Sets the next processor in the chain.
     *
     * @param next the next processor to be set
     */
    void setNext(ChainProcessor<T, R> next);

    /**
     * Gets the next processor in the chain.
     *
     * @return an Optional containing the next processor if it exists, otherwise an empty Optional
     */
    Optional<ChainProcessor<T, R>> getNext();

    /***
     * Creates a processor that checks if the next processor should be invoked based on the eligibilityChecker.
     * If the eligibilityChecker returns a non-empty Optional, the next processor is invoked with the new request.
     * Otherwise, the current response is returned
     *
     * @param eligibilityChecker a BiFunction that takes the current response and request, and returns an Optional containing
     *                           a new ChainProcessorRequest if the next processor should be invoked, or an empty Optional otherwise
     * @return a BiFunction that takes the current response and request, and returns the final response after processing
     */
    default BiFunction<R, ChainProcessorRequest<T, R>, R> nextProcessor(BiFunction<R, T, Optional<ChainProcessorRequest<T, R>>> eligibilityChecker) {
        return (currentResponse, currentRequest) -> {
            return eligibilityChecker.apply(currentResponse, currentRequest.request())
                    .map(this::forward)
                    .orElse(currentResponse);
        };
    }

    /**
     * Forwards the request to the next processor in the chain if it exists.
     * If there is no next processor, returns the previous response from the request.
     *
     * @param request the request to be forwarded
     * @return the response from the next processor or the previous response if no next processor exists
     */
    private R forward(ChainProcessorRequest<T, R> request) {
        return getNext()
                .map(nextProcessor -> nextProcessor.process(request))
                .orElseGet(request::previousResponse);
    }

    /**
     * A record representing a request to be processed by a ChainProcessor.
     *
     * @param <T> the type of the request
     * @param <R> the type of the previous response
     */
    record ChainProcessorRequest<T, R>(T request, R previousResponse) {
    }
}
