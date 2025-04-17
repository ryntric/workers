## Worker Service

The Worker Service - provides the ability to execute tasks by the same key on the same thread.

When is this library helpful!?
When you have to handle a lot of real-time events from a message broker or tasks without using locks and extra synchronization.

An example of usage:

```
OnWorkerTaskCompletion onWorkerTaskCompletion = new OnWorkerTaskCompletion() {
            private final Map<String, LongAdder> counters = new ConcurrentHashMap<>();
            private final LongSummaryStatistics statistics = new LongSummaryStatistics();

            private void increment(String workerName, Map<String, Object> attributes) {
                counters.computeIfAbsent(workerName, k -> new LongAdder()).increment();
                statistics.accept(Workers.<Long>getTaskAttribute(attributes, WorkerTaskAttributes.COMPLETED_AT) - Workers.<Long>getTaskAttribute(attributes, WorkerTaskAttributes.STARTED_AT));
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
        
Runnable task = () -> System.out.println("Doing something");    

try (WorkerService workerService = Workers.newWorkerService("test", 4, 400, Hashing.murmur3_32_fixed(), onWorkerTaskCompletion)) {
    CompletableFuture<?>[] futures = IntStream.range(0, 100_000_000)
        .mapToObj(i -> workerService.execute(String.valueOf(UUID.randomUUID()), task))
        .toArray(CompletableFuture[]::new);
    CompletableFuture.allOf(futures).join();
}
```
