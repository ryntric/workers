package io.github.viacheslavbondarchuk;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public final class CallableTask<R> extends AbstractWorkerTask<R> {
    private final Callable<R> callable;

    public CallableTask(String key, CompletableFuture<R> future, Callable<R> callable) {
        super(key, future);
        this.callable = callable;
    }

    @Override
    public R execute() throws Exception {
        return callable.call();
    }
}
