package io.github.viacheslavbondarchuk;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public final class CallableTask<R> extends AbstractWorkerTask<R> {
    private final Callable<R> callable;

    public CallableTask(String key, CompletableFuture<R> future, Callable<R> callable, Map<String, Object> attributes) {
        super(key, future, attributes);
        this.callable = callable;
    }

    @Override
    R _execute() throws Exception {
        return callable.call();
    }
}
