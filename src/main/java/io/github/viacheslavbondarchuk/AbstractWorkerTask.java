package io.github.viacheslavbondarchuk;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractWorkerTask<R> implements WorkerTask<R> {
    private final CompletableFuture<R> future;
    private final String key;

    public AbstractWorkerTask(String key, CompletableFuture<R> future) {
        this.key = key;
        this.future = future;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public CompletableFuture<R> getCompletableFuture() {
        return future;
    }

}
