package io.github.ryntric;

import com.lmax.disruptor.EventTranslatorTwoArg;

import java.util.concurrent.CompletableFuture;

final class WorkerTaskEventTranslator implements EventTranslatorTwoArg<WorkerTaskEvent, WorkerTask<?>, CompletableFuture<?>> {
    public static final WorkerTaskEventTranslator INSTANCE = new WorkerTaskEventTranslator();

    private WorkerTaskEventTranslator() {}

    @Override
    public void translateTo(WorkerTaskEvent event, long sequence, WorkerTask<?> task, CompletableFuture<?> future) {
        event.setTask(task);
        event.setFuture(future);
    }
}
