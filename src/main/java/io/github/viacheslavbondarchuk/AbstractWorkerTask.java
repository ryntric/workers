package io.github.viacheslavbondarchuk;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static io.github.viacheslavbondarchuk.WorkerTaskAttributes.COMPLETED_AT;
import static io.github.viacheslavbondarchuk.WorkerTaskAttributes.CREATED_AT;
import static io.github.viacheslavbondarchuk.WorkerTaskAttributes.STARTED_AT;

public abstract class AbstractWorkerTask<R> implements WorkerTask<R> {
    private final Map<String, Object> attributes;
    private final CompletableFuture<R> future;
    private final String key;

    public AbstractWorkerTask(String key, CompletableFuture<R> future, Map<String, Object> attributes) {
        this.attributes = new HashMap<>(attributes);
        this.key = key;
        this.future = future;
        this.init();
    }

    private void init() {
        attributes.put(CREATED_AT, Workers.getCurrentTimeInMillis());
    }

    abstract R _execute() throws Exception;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public final R execute() throws Exception {
        attributes.put(STARTED_AT, Workers.getCurrentTimeInMillis());
        try {
            return _execute();
        } finally {
            attributes.put(COMPLETED_AT, Workers.getCurrentTimeInMillis());
        }
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public CompletableFuture<R> getCompletableFuture() {
        return future;
    }

}
