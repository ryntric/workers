package io.github.viacheslavbondarchuk;

import java.util.concurrent.CompletableFuture;

interface WorkerTask<R> {
    R execute() throws Exception;

    String getKey();

    CompletableFuture<R> getCompletableFuture();
}
