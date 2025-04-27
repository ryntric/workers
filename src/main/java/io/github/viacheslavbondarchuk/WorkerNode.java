package io.github.viacheslavbondarchuk;

import com.lmax.disruptor.dsl.Disruptor;

final class WorkerNode {
    private final String name;
    private final Disruptor<WorkerTaskEvent> worker;

    WorkerNode(String name, Disruptor<WorkerTaskEvent> worker) {
        this.name = name;
        this.worker = worker;
    }

    void execute(WorkerTask<?> workerTask) {
        worker.publishEvent(WorkerTaskEventTranslator.getInstance(), workerTask);
    }

    String getName() {
        return name;
    }

}
