package io.github.ryntric;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("rawtypes")
final class WorkerTaskEvent {
    private WorkerTask task;
    private CompletableFuture future;

    WorkerTask<?> getTask() {
        return task;
    }

    public CompletableFuture<?> getFuture() {
        return future;
    }

    void setTask(WorkerTask<?> task) {
        this.task = task;
    }

    void setFuture(CompletableFuture<?> future) {
        this.future = future;
    }

    boolean isCancelled() {
        return future.isCancelled();
    }

    void clear() {
        this.task = null;
        this.future = null;
    }
}
