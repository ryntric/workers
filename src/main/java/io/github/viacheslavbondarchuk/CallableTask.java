package io.github.viacheslavbondarchuk;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

final class CallableTask<R> extends AbstractWorkerTask<R> {
    private final Callable<R> callable;

    CallableTask(String key, CompletableFuture<R> future, Callable<R> callable) {
        this(key, future, callable, null);
    }

    CallableTask(String key, CompletableFuture<R> future, Callable<R> callable, String name) {
        super(key, future, name);
        this.callable = callable;
    }

    @Override
    public R execute() throws Exception {
        return callable.call();
    }
}
