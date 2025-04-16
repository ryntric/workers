package io.github.viacheslavbondarchuk;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

interface WorkerTask<R> {
    R execute() throws Exception;

    String getKey();

    Map<String, Object> getAttributes();

    CompletableFuture<R> getCompletableFuture();
}
