## Worker Service

Workers - the library that provides an ability to execute tasks by the same key on the same thread.
This library is based on the [Disruptor](https://lmax-exchange.github.io/disruptor/),
each worker is the separated Disruptor with own task buffer.  

When is this library helpful!? When you have to handle a lot of real-time events from a message broker or tasks without using locks and extra synchronization.

An example of usage:

```java
private static final Runnable task = () -> System.out.printf("Worker %s doing something%n", Thread.currentThread().getName());
private static final WorkerServiceConfig config = WorkerServiceConfig.builder()
        .name("test-service")
        .replicaCount(2000)
        .bufferSize(8192)
        .build();

public static void main(String[] args) {
    try (DisruptorWorkerService workerService = new DisruptorWorkerService(config)) {
        CompletableFuture<?>[] futures = IntStream.range(0, 5_000_000)
                .parallel()
                .mapToObj(ignored -> workerService.execute(String.valueOf(UUID.randomUUID()), task))
                .toArray(CompletableFuture<?>[]::new);
        CompletableFuture.allOf(futures).join();
    }
}
```
