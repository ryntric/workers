package io.github.ryntric;

import io.github.ryntric.EventTranslator.EventTranslatorFiveArg;
import io.github.ryntric.EventTranslator.EventTranslatorFourArg;
import io.github.ryntric.EventTranslator.EventTranslatorOneArg;
import io.github.ryntric.EventTranslator.EventTranslatorThreeArg;
import io.github.ryntric.EventTranslator.EventTranslatorTwoArg;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.concurrent.CompletableFuture;

class WorkerServiceTest {
    private static final EventHandler<Event> handler = new EventHandler<>() {
        @Override
        public void onEvent(String name, Event event, long sequence) {
            CompletableFuture<?> future = event.getFuture();
            future.complete(null);
        }

        @Override
        public void onError(String name, Event event, long sequence, Throwable ex) {
            throw new RuntimeException(ex);
        }

        @Override
        public void onStart(String name) {

        }

        @Override
        public void onShutdown(String name) {

        }
    };
    private static final WorkerService<Event> workerService = new WorkerService<>("test", handler, Event::new, DefaultHashCodeProvider.INSTANCE, WorkerServiceConfig.INSTANCE);
    private static final EventTranslatorOneArg<Event, CompletableFuture<?>> TRANSLATOR_ONE_ARG = Event::setFuture;
    private static final EventTranslatorTwoArg<Event, CompletableFuture<?>, Object> TRANSLATOR_TWO_ARG = (event, arg0, arg1) ->  event.setFuture(arg0);
    private static final EventTranslatorThreeArg<Event, CompletableFuture<?>, Object, Object> TRANSLATOR_THREE_ARG =  (event, arg0, arg1, arg2) -> event.setFuture(arg0);
    private static final EventTranslatorFourArg<Event, CompletableFuture<?>, Object, Object, Object> TRANSLATOR_FOUR_ARG = (event, arg0, arg1, arg2, arg3) -> event.setFuture(arg0);
    private static final EventTranslatorFiveArg<Event, CompletableFuture<?>, Object, Object, Object, Object> TRANSLATOR_FIVE_ARG = (event, arg0, arg1, arg2, arg3, arg4) -> event.setFuture(arg0);

    private static final String STR_KEY = "key";
    private static final int INT_KEY = 0;
    private static final long LONG_KEY = 1;
    private static final byte[] BYTE_KEY = new byte[]{0x7F};

    @BeforeAll
    public static void setup() {
        workerService.start();
    }

    @AfterAll
    public static void teardown() {
        workerService.shutdown();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith1ArgForStrKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(STR_KEY, TRANSLATOR_ONE_ARG, future);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith1ArgForIntKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(INT_KEY, TRANSLATOR_ONE_ARG, future);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith1ArgForLongKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(LONG_KEY, TRANSLATOR_ONE_ARG, future);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith1ArgForByteKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(BYTE_KEY, TRANSLATOR_ONE_ARG, future);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith1ArgForStrKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvents(STR_KEY, TRANSLATOR_ONE_ARG, futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith1ArgForIntKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvents(INT_KEY, TRANSLATOR_ONE_ARG, futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith1ArgForLongKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvents(LONG_KEY, TRANSLATOR_ONE_ARG, futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith1ArgForByteKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvents(BYTE_KEY, TRANSLATOR_ONE_ARG, futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith2ArgsForStrKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(STR_KEY, TRANSLATOR_TWO_ARG, future, null);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith2ArgsForIntKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(INT_KEY, TRANSLATOR_TWO_ARG, future, null);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith2ArgsForLongKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(LONG_KEY, TRANSLATOR_TWO_ARG, future, null);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith2ArgsForByteKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(BYTE_KEY, TRANSLATOR_TWO_ARG, future, null);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith2ArgForStrKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvents(STR_KEY, TRANSLATOR_TWO_ARG, futures,  futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith2ArgsForIntKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvents(INT_KEY, TRANSLATOR_TWO_ARG, futures,  futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith2ArgsForLongKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvents(LONG_KEY, TRANSLATOR_TWO_ARG, futures,  futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith2ArgsForByteKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvents(BYTE_KEY, TRANSLATOR_TWO_ARG, futures,  futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith3ArgsForStrKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(STR_KEY, TRANSLATOR_THREE_ARG, future, null, null);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith3ArgsForIntKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(INT_KEY, TRANSLATOR_THREE_ARG, future, null, null);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith3ArgsForLongKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(LONG_KEY, TRANSLATOR_THREE_ARG, future, null, null);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith3ArgsForByteKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(BYTE_KEY, TRANSLATOR_THREE_ARG, future, null, null);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith3ArgsForStrKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvents(STR_KEY, TRANSLATOR_THREE_ARG, futures, futures, futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith3ArgsForIntKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvents(INT_KEY, TRANSLATOR_THREE_ARG, futures, futures, futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith3ArgsForLongKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvents(LONG_KEY, TRANSLATOR_THREE_ARG, futures, futures, futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith3ArgsForByteKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvents(BYTE_KEY, TRANSLATOR_THREE_ARG, futures, futures, futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith4ArgsForStrKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(STR_KEY, TRANSLATOR_FOUR_ARG, future, null, null, null);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith4ArgsForIntKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(INT_KEY, TRANSLATOR_FOUR_ARG, future, null, null, null);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith4ArgsForLongKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(LONG_KEY, TRANSLATOR_FOUR_ARG, future, null, null, null);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith4ArgsForByteKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(BYTE_KEY, TRANSLATOR_FOUR_ARG, future, null, null, null);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith4ArgsForStrKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvents(STR_KEY, TRANSLATOR_FOUR_ARG, futures, futures, futures, futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith4ArgsForIntKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvents(INT_KEY, TRANSLATOR_FOUR_ARG, futures, futures, futures, futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith4ArgsForLongKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvents(LONG_KEY, TRANSLATOR_FOUR_ARG, futures, futures, futures, futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith4ArgsForByteKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvents(BYTE_KEY, TRANSLATOR_FOUR_ARG, futures, futures, futures, futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith5ArgsForStrKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(STR_KEY, TRANSLATOR_FIVE_ARG, future, null, null, null, null);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith5ArgsForIntKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(INT_KEY, TRANSLATOR_FIVE_ARG, future, null, null, null, null);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith5ArgsForLongKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(LONG_KEY, TRANSLATOR_FIVE_ARG, future, null, null, null, null);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventWith5ArgsForByteKey() {
        CompletableFuture<?> future = new CompletableFuture<>();
        workerService.publishEvent(BYTE_KEY, TRANSLATOR_FIVE_ARG, future, null, null, null, null);
        future.join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith5ArgsForStrKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvent(STR_KEY, TRANSLATOR_FIVE_ARG, futures, futures, futures, futures, futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith5ArgsForIntKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvent(INT_KEY, TRANSLATOR_FIVE_ARG, futures, futures, futures, futures, futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith5ArgsForLongKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvent(LONG_KEY, TRANSLATOR_FIVE_ARG, futures, futures, futures, futures, futures);
        CompletableFuture.allOf(futures).join();
    }

    @Test
    @Execution(ExecutionMode.CONCURRENT)
    public void testPublishEventBatchWith5ArgsForByteKey() {
        CompletableFuture<?>[] futures = {new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>(), new CompletableFuture<>()};
        workerService.publishEvent(BYTE_KEY, TRANSLATOR_FIVE_ARG, futures, futures, futures, futures, futures);
        CompletableFuture.allOf(futures).join();
    }

    private static class Event {
        private CompletableFuture<?> future;

        public CompletableFuture<?> getFuture() {
            return future;
        }

        public void setFuture(CompletableFuture<?> future) {
            this.future = future;
        }
    }

}
