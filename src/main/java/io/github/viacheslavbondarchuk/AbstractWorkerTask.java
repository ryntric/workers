package io.github.viacheslavbondarchuk;

import java.util.concurrent.CompletableFuture;

abstract class AbstractWorkerTask<R> implements WorkerTask<R> {
    private final Long createdAt = ClockUtil.inMillis();

    private final CompletableFuture<R> future;
    private final String key;
    private final String name;

    AbstractWorkerTask(String key, CompletableFuture<R> future, String name) {
        this.key = key;
        this.future = future;
        this.name = name;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Long getCreatedAt() {
        return createdAt;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public void complete(R result) {
        future.complete(result);
    }

    @Override
    public void completeExceptionally(Throwable ex) {
        future.completeExceptionally(ex);
    }
}
