package io.github.ryntric;

import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.CompletableFuture;

final class WorkerNode {
    private final String name;
    private final Disruptor<WorkerTaskEvent> worker;

    WorkerNode(String name, Disruptor<WorkerTaskEvent> worker) {
        this.name = name;
        this.worker = worker;
    }

    void execute(WorkerTask<?> workerTask, CompletableFuture<?> future) {
        worker.publishEvent(WorkerTaskEventTranslator.getInstance(), workerTask, future);
    }

    String getName() {
        return name;
    }

}
