package io.github.viacheslavbondarchuk;

import com.google.common.hash.Hashing;

import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

import static io.github.viacheslavbondarchuk.WorkerTaskAttributes.COMPLETED_AT;
import static io.github.viacheslavbondarchuk.WorkerTaskAttributes.STARTED_AT;

public class Main {
    public static void main(String[] args) {
        OnWorkerTaskCompletion onWorkerTaskCompletion = new OnWorkerTaskCompletion() {
            public final Map<String, LongAdder> counters = new ConcurrentHashMap<>();
            public final LongSummaryStatistics stats = new LongSummaryStatistics();

            private void increment(String workerName, Map<String, Object> attributes) {
                counters.computeIfAbsent(workerName, k -> new LongAdder()).increment();
                stats.accept(Workers.<Long>getTaskAttribute(attributes, COMPLETED_AT) - Workers.<Long>getTaskAttribute(attributes, STARTED_AT));
            }

            @Override
            public void onSuccess(String key, String workerName, Map<String, Object> attributes) {
                increment(workerName, attributes);
            }

            @Override
            public void onCancel(String key, String workerName, Map<String, Object> attributes) {
                increment(workerName, attributes);
            }

            @Override
            public void onError(String key, String workerName, Exception ex, Map<String, Object> attributes) {
                increment(workerName, attributes);
            }
        };

        Runnable task = () -> System.out.println("Doing something...");

        WorkerService workerService = Workers.newWorkerService("test", 2, 5000, Hashing.murmur3_32_fixed(), onWorkerTaskCompletion);
        CompletableFuture<?>[] futures = IntStream.range(0, 1_000_000)
                .mapToObj(i -> workerService.execute(UUID.randomUUID().toString(), task))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        workerService.shutdown();
    }
}
