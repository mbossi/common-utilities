package com.ia.common.utilities.helpher;

import com.ia.common.utilities.helper.AsyncHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsyncHelperTest {

    private AsyncHelper helper;

    @Mock
    private TestMessage operationVerifier;

    @Mock
    private TestMessage executionVerifier;

    @BeforeEach
    void setup() {
        final Executor executor = new DefaultExecutor(executionVerifier);
        helper = new AsyncHelper(executor);
    }

    @Test
    void testRunWithCallback() {
        helper.run(() -> operationVerifier.verifyFirstAction(), testMessage -> operationVerifier.verifyCallback());
        verify(operationVerifier).verifyFirstAction();
        verify(operationVerifier).verifyCallback();
        verify(executionVerifier).setMessage("Executor have been triggered");
        verify(executionVerifier).setMessage("Execution completed");
        operationVerifier.verifyFirstAction();
    }

    @Test
    void testGetResultFromFuture() {
        final CompletableFuture<String> future = helper.run(() -> "Test Message");
        final Optional<String> result = AsyncHelper.get(future);
        assertThat(result).hasValue("Test Message");
    }

    @Test
    void testGetResultFromFutureWithException() {
        final CompletableFuture<String> future = helper.run(() -> {
            throw new RuntimeException("Test Exception");
        });
        final Optional<String> result = AsyncHelper.get(future);
        assertThat(result).isEmpty();
    }

    @RequiredArgsConstructor
    static class DefaultExecutor implements Executor {
        private final TestMessage callback;

        @Override
        public void execute(Runnable command) {
            callback.setMessage("Executor have been triggered");
            command.run();
            callback.setMessage("Execution completed");
        }
    }

    @Getter
    @Setter
    static class TestMessage {
        private String message;

        public TestMessage verifyFirstAction() {
            return this;
        }

        public void verifyCallback() {
        }
    }
}
