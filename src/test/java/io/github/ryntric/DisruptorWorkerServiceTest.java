package io.github.ryntric;

import com.lmax.disruptor.InsufficientCapacityException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

class DisruptorWorkerServiceTest {
    public static final WorkerServiceConfig config = WorkerServiceConfig.builder().bufferSize(1).name("test").build();
    private static final WorkerService workerService = new DisruptorWorkerService(config);

    @AfterAll
    static void tearDown() {
        workerService.shutdown();
    }

    private static RunnableTask newCounterTask(LongAdder counter) {
        return new RunnableTask() {
            @Override
            public Void execute() {
                counter.increment();
                return null;
            }
        };
    }

    private static RunnableTask newLongCounterTask(LongAdder counter) {
        return new RunnableTask() {

            @Override
            public Void execute() {
                counter.increment();
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }
        };
    }

    private static <T> CallableTask<T> newCallableTask(T value) {
        return new CallableTask<T>() {
            @Override
            public T execute() {
                return value;
            }
        };
    }

    private static <T> CallableTask<T> newLongCallableTask(T value) {
        return new CallableTask<T>() {
            @Override
            public T execute() {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return value;
            }
        };
    }

    @Test
    void executeVoidTaskThatThrowsException() {
        CompletableFuture<Void> future = workerService.execute(String.valueOf(UUID.randomUUID()), new RunnableTask() {
            @Override
            public Void execute() {
                throw new RuntimeException("Test exception");
            }
        });
        Assertions.assertThrowsExactly(ExecutionException.class, future::get);
        Assertions.assertTrue(future.isCompletedExceptionally());
    }

    @Test
    void executeCallableTaskThatThrowsException() {
        CompletableFuture<Object> future = workerService.execute(String.valueOf(UUID.randomUUID()), new CallableTask<>() {
            @Override
            public Object execute() {
                throw new RuntimeException("Test exception");
            }
        });

        Assertions.assertThrowsExactly(ExecutionException.class, future::get);
        Assertions.assertTrue(future.isCompletedExceptionally());
    }

    @Test
    void executeVoidTaskByStringKey() {
        LongAdder counter = new LongAdder();
        CompletableFuture<Void> future = workerService.execute(String.valueOf(UUID.randomUUID()), newCounterTask(counter));
        future.join();
        Assertions.assertEquals(1, counter.longValue());
    }

    @Test
    void executeVoidTaskByIngerKey() {
        LongAdder counter = new LongAdder();
        CompletableFuture<Void> future = workerService.execute(1, newCounterTask(counter));
        future.join();
        Assertions.assertEquals(1, counter.longValue());
    }

    @Test
    void executeVoidTaskByLongKey() {
        LongAdder counter = new LongAdder();
        CompletableFuture<Void> future = workerService.execute(1L, newCounterTask(counter));
        future.join();
        Assertions.assertEquals(1, counter.longValue());
    }

    @Test
    void executeVoidTaskByByteKey() {
        LongAdder counter = new LongAdder();
        CompletableFuture<Void> future = workerService.execute(new byte[]{0b1}, newCounterTask(counter));
        future.join();
        Assertions.assertEquals(1, counter.longValue());
    }

    @Test
    void tryExecuteRunnableTaskByStringKey() throws ExecutionException, InterruptedException, InsufficientCapacityException {
        LongAdder counter = new LongAdder();
        CompletableFuture<Void> future = workerService.tryExecute("test", newCounterTask(counter));
        future.join();
        Assertions.assertEquals(1, counter.longValue());
    }

    @Test
    void tryExecuteRunnableTaskByIntegerKey() throws ExecutionException, InterruptedException, InsufficientCapacityException {
        LongAdder counter = new LongAdder();
        CompletableFuture<Void> future = workerService.tryExecute(1, newCounterTask(counter));
        future.join();
        Assertions.assertEquals(1, counter.longValue());
    }

    @Test
    void tryExecuteRunnableTaskByLongKey() throws ExecutionException, InterruptedException, InsufficientCapacityException {
        LongAdder counter = new LongAdder();
        CompletableFuture<Void> future = workerService.tryExecute(1L, newCounterTask(counter));
        future.join();
        Assertions.assertEquals(1, counter.longValue());
    }

    @Test
    void tryExecuteRunnableTaskByByteKey() throws ExecutionException, InterruptedException, InsufficientCapacityException {
        LongAdder counter = new LongAdder();
        CompletableFuture<Void> future = workerService.tryExecute(new byte[]{0b1}, newCounterTask(counter));
        future.join();
        Assertions.assertEquals(1, counter.longValue());
    }

    @Test
    void tryExecuteRunnableTaskByStringKeyWhenRingBufferIsFull() throws ExecutionException, InterruptedException, InsufficientCapacityException {
        LongAdder counter = new LongAdder();
        CompletableFuture<Void> future = workerService.tryExecute("test", newLongCounterTask(counter));
        Assertions.assertThrowsExactly(InsufficientCapacityException.class, () -> workerService.tryExecute("test", newLongCounterTask(counter)));

        future.join();
        Assertions.assertEquals(1, counter.longValue());
    }

    @Test
    void tryExecuteRunnableTaskByIntegerKeyWhenRingBufferIsFull() throws ExecutionException, InterruptedException, InsufficientCapacityException {
        LongAdder counter = new LongAdder();
        CompletableFuture<Void> future = workerService.tryExecute(1, newLongCounterTask(counter));
        Assertions.assertThrowsExactly(InsufficientCapacityException.class, () -> workerService.tryExecute(1, newLongCounterTask(counter)));

        future.join();
        Assertions.assertEquals(1, counter.longValue());
    }

    @Test
    void tryExecuteRunnableTaskByLongKeyWhenRingBufferIsFull() throws ExecutionException, InterruptedException, InsufficientCapacityException {
        LongAdder counter = new LongAdder();
        CompletableFuture<Void> future = workerService.tryExecute(1L, newLongCounterTask(counter));
        Assertions.assertThrowsExactly(InsufficientCapacityException.class, () -> workerService.tryExecute(1L, newLongCounterTask(counter)));

        future.join();
        Assertions.assertEquals(1, counter.longValue());
    }

    @Test
    void tryExecuteRunnableTaskByByteKeyWhenRingBufferIsFull() throws ExecutionException, InterruptedException, InsufficientCapacityException {
        LongAdder counter = new LongAdder();
        byte[] key = {0b1};
        CompletableFuture<Void> future = workerService.tryExecute(key, newLongCounterTask(counter));
        Assertions.assertThrowsExactly(InsufficientCapacityException.class, () -> workerService.tryExecute(key, newLongCounterTask(counter)));

        future.join();
        Assertions.assertEquals(1, counter.longValue());
    }

    @Test
    void executeCallableTaskByStringKey() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = workerService.execute(String.valueOf(UUID.randomUUID()), newCallableTask("str"));
        Assertions.assertEquals("str", future.get());
    }

    @Test
    void executeCallableTaskByIntegerKey() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = workerService.execute(1, newCallableTask("str"));
        Assertions.assertEquals("str", future.get());
    }

    @Test
    void executeCallableTaskByLongKey() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = workerService.execute(1L, newCallableTask("str"));
        Assertions.assertEquals("str", future.get());
    }

    @Test
    void executeCallableTaskByByteKey() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = workerService.execute(new byte[]{0b1}, newCallableTask("str"));
        Assertions.assertEquals("str", future.get());
    }

    @Test
    void tryExecuteCallableTaskByStringKey() throws ExecutionException, InterruptedException, InsufficientCapacityException {
        CompletableFuture<String> future = workerService.tryExecute("test", newCallableTask("str"));
        Assertions.assertEquals("str", future.get());
    }

    @Test
    void tryExecuteCallableTaskByIntegerKey() throws ExecutionException, InterruptedException, InsufficientCapacityException {
        CompletableFuture<String> future = workerService.tryExecute(1, newCallableTask("str"));
        Assertions.assertEquals("str", future.get());
    }

    @Test
    void tryExecuteCallableTaskByLongKey() throws ExecutionException, InterruptedException, InsufficientCapacityException {
        CompletableFuture<String> future = workerService.tryExecute(1L, newCallableTask("str"));
        Assertions.assertEquals("str", future.get());
    }

    @Test
    void tryExecuteCallableTaskByByteKey() throws ExecutionException, InterruptedException, InsufficientCapacityException {
        CompletableFuture<String> future = workerService.tryExecute(new byte[]{0b1}, newCallableTask("str"));
        Assertions.assertEquals("str", future.get());
    }

    @Test
    void tryExecuteCallableTaskByStringKeyWhenRingBufferIsFull() throws ExecutionException, InterruptedException, InsufficientCapacityException {
        CompletableFuture<String> future = workerService.tryExecute("test", newLongCallableTask("str"));

        Assertions.assertThrowsExactly(InsufficientCapacityException.class, () -> workerService.tryExecute("test", newLongCallableTask("str")));
        Assertions.assertEquals("str", future.get());
        future.join();
    }

    @Test
    void tryExecuteCallableTaskByIntegerKeyWhenRingBufferIsFull() throws ExecutionException, InterruptedException, InsufficientCapacityException {
        CompletableFuture<String> future = workerService.tryExecute(1, newLongCallableTask("str"));

        Assertions.assertThrowsExactly(InsufficientCapacityException.class, () -> workerService.tryExecute(1, newLongCallableTask("str")));
        Assertions.assertEquals("str", future.get());
        future.join();
    }

    @Test
    void tryExecuteCallableTaskByLongKeyWhenRingBufferIsFull() throws ExecutionException, InterruptedException, InsufficientCapacityException {
        CompletableFuture<String> future = workerService.tryExecute(1L, newLongCallableTask("str"));

        Assertions.assertThrowsExactly(InsufficientCapacityException.class, () -> workerService.tryExecute(1L, newLongCallableTask("str")));
        Assertions.assertEquals("str", future.get());
        future.join();
    }

    @Test
    void tryExecuteCallableTaskByByteKeyWhenRingBufferIsFull() throws ExecutionException, InterruptedException, InsufficientCapacityException {
        CompletableFuture<String> future = workerService.tryExecute(new byte[]{0b1}, newLongCallableTask("str"));

        Assertions.assertThrowsExactly(InsufficientCapacityException.class, () -> workerService.tryExecute(new byte[]{0b1}, newLongCallableTask("str")));
        Assertions.assertEquals("str", future.get());
        future.join();
    }

    @Test
    void getWorkerCount() {
        Assertions.assertEquals(WorkerUtil.getAvailableProcessors(), workerService.getWorkerCount());
    }

    @Test
    void getWorkerNodeCount() {
        Assertions.assertEquals(200, workerService.getWorkerNodeCount());
    }

    @Test
    void shutdown() {
        DisruptorWorkerService disruptorWorkerService = new DisruptorWorkerService(config);
        disruptorWorkerService.shutdown();
        Assertions.assertThrowsExactly(WorkerServiceDeadException.class, () -> disruptorWorkerService.execute(String.valueOf(UUID.randomUUID()),
                newCallableTask("str")));
    }

}
