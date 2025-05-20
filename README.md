[![LICENSE](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/ryntric/workers/blob/master/LICENSE)
[![Maven CI](https://github.com/ryntric/workers/actions/workflows/workflow.yml/badge.svg)](https://github.com/ryntric/workers/actions/workflows/workflow.yml)
[![pages-build-deployment](https://github.com/ryntric/workers/actions/workflows/pages/pages-build-deployment/badge.svg)](https://github.com/ryntric/workers/actions/workflows/pages/pages-build-deployment)

Workers — a library that provides an ability to execute tasks by the same key on the same thread
What is a main purpose of this library? Provides a complete solution when you need to handle tasks by key without the overhead of locks and synchronization.

For example, you have a few Kafka listeners that consume some events and process them.
All are fine when you don't need to put their events into memory cache and handle them in parallel,
but when you need to boost your application performance and throughput, you should use multithreading.
In a simple scenario you can use locks and synchronization, but it also decreases performance at the same time.
So it's a simple example of when the Workers library is helpful.

Example of usage:

```java
import io.github.ryntric.ClockUtil;
import io.github.ryntric.DisruptorWorkerService;
import io.github.ryntric.RunnableTask;
import io.github.ryntric.WorkerService;
import io.github.ryntric.WorkerServiceConfig;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

private static final WorkerServiceConfig config = WorkerServiceConfig.builder().name("test").replicaCount(400).bufferSize(8192).build();
private static final RunnableTask task = new RunnableTask() {
    @Override
    public Void execute() {
        System.out.println("Worker " + Thread.currentThread()
                .getName() + " doing something...");
        return null;
    }
};

public static void main(String[] args) throws IOException {
    long started = ClockUtil.inMillis();
    try (WorkerService workerService = new DisruptorWorkerService(config)) {
        CompletableFuture<?>[] futures = IntStream.range(0, 5_000_000)
                .parallel()
                .mapToObj(key -> workerService.execute(key, task))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        System.out.println("Completed " + futures.length + " tasks, elapsed time " + ClockUtil.diffMillis(started) + " ms");
    }
}
```
