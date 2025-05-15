package io.github.ryntric;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

class DisruptorWorkerServiceTest {
    public static final WorkerServiceConfig config = WorkerServiceConfig.builder().name("test").build();
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

    private static <T> CallableTask<T> newCallableTask(T value) {
        return new CallableTask<T>() {
            @Override
            public T execute() {
                return value;
            }
        };
    }

    @Test
    void getWorkerNodeId() {
        String key = String.valueOf(UUID.randomUUID());

        long result = IntStream.range(0, 1000)
                .mapToLong(i -> workerService.getWorkerNodeId(WorkerUtil.getKeyHash(key, config.getHashFunction())))
                .reduce((left, right) -> left ^ right)
                .getAsLong();

        Assertions.assertEquals(0, result);
    }

    @Test
    void getWorkerNode() {
        String key = String.valueOf(UUID.randomUUID());

        long result = IntStream.range(0, 1000)
                .mapToObj(workerNodeId -> workerService.getWorkerNode(WorkerUtil.getKeyHash(key, config.getHashFunction())))
                .mapToLong(WorkerNode::hashCode)
                .reduce((left, right) -> left ^ right)
                .getAsLong();

        Assertions.assertEquals(0, result);
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
        CompletableFuture<String> future = workerService.execute(new byte[] {0b1}, newCallableTask("str"));
        Assertions.assertEquals("str", future.get());
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
        Assertions.assertThrowsExactly(WorkerServiceDeadException.class,
                () -> disruptorWorkerService.execute(String.valueOf(UUID.randomUUID()), newCallableTask("str")));
    }

}
