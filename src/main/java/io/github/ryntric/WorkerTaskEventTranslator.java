package io.github.ryntric;

import com.lmax.disruptor.EventTranslatorTwoArg;

import java.util.concurrent.CompletableFuture;

final class WorkerTaskEventTranslator implements EventTranslatorTwoArg<WorkerTaskEvent, WorkerTask<?>, CompletableFuture<?>> {

    private WorkerTaskEventTranslator() {}

    public static WorkerTaskEventTranslator getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void translateTo(WorkerTaskEvent event, long sequence, WorkerTask<?> task, CompletableFuture<?> future) {
        event.setTask(task);
        event.setFuture(future);
    }

    private static class InstanceHolder {
        private static final WorkerTaskEventTranslator INSTANCE = new WorkerTaskEventTranslator();
    }
}
