package io.github.viacheslavbondarchuk;

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

    @Test
    void getWorkerNodeId() {
        String key = String.valueOf(UUID.randomUUID());

        long result = IntStream.range(0, 1000)
                .mapToLong(i -> workerService.getWorkerNodeId(key))
                .reduce((left, right) -> left ^ right)
                .getAsLong();

        Assertions.assertEquals(0, result);
    }

    @Test
    void getWorkerNode() {
        String key = String.valueOf(UUID.randomUUID());

        long result = IntStream.range(0, 1000)
                .mapToObj(workerNodeId -> workerService.getWorkerNode(key))
                .mapToLong(WorkerNode::hashCode)
                .reduce((left, right) -> left ^ right)
                .getAsLong();

        Assertions.assertEquals(0, result);
    }

    @Test
    void executeVoidTask() {
        LongAdder counter = new LongAdder();
        CompletableFuture<Void> future = workerService.execute(String.valueOf(UUID.randomUUID()), counter::increment);
        future.join();
        Assertions.assertEquals(1, counter.longValue());
    }

    @Test
    void executeCallableTask() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = workerService.execute(String.valueOf(UUID.randomUUID()), () -> "str");
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
                () -> disruptorWorkerService.execute(String.valueOf(UUID.randomUUID()), () -> "str"));
    }

}
