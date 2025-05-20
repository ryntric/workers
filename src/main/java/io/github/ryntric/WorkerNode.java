package io.github.ryntric;

import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.RingBuffer;

import java.util.concurrent.CompletableFuture;

final class WorkerNode {
    private final String name;
    private final RingBuffer<WorkerTaskEvent> ringBuffer;

    WorkerNode(String name, RingBuffer<WorkerTaskEvent> ringBuffer) {
        this.name = name;
        this.ringBuffer = ringBuffer;
    }

    void execute(WorkerTask<?> workerTask, CompletableFuture<?> future) {
        ringBuffer.publishEvent(WorkerTaskEventTranslator.INSTANCE, workerTask, future);
    }

    void tryExecute(WorkerTask<?> workerTask, CompletableFuture<?> future) throws InsufficientCapacityException {
        long sequence = ringBuffer.tryNext();
        WorkerTaskEventTranslator.INSTANCE.translateTo(ringBuffer.get(sequence), sequence, workerTask, future);
        ringBuffer.publish(sequence);
    }

    String getName() {
        return name;
    }

}
