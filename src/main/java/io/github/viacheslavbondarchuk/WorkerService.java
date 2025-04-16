package io.github.viacheslavbondarchuk;

import java.io.Closeable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public interface WorkerService extends AutoCloseable, Closeable {

    /**
     * Executes task and returns CompletableFuture
     *
     * @param key      - A task distribution key
     * @param runnable - A task that will be executed on the selected worker by key
     * @return {@link  java.util.concurrent.CompletableFuture}
     */
    default CompletableFuture<Void> execute(String key, Runnable runnable) {
        return execute(key, runnable, Collections.emptyMap());
    }

    /**
     * Executes task and returns CompletableFuture
     *
     * @param key        - A task distribution key
     * @param runnable   - A task that will be executed on the selected worker by key
     * @param attributes - Additional task attributes
     * @return {@link  java.util.concurrent.CompletableFuture}
     */
    CompletableFuture<Void> execute(String key, Runnable runnable, Map<String, Object> attributes);

    /**
     * Executes task and returns CompletableFuture
     *
     * @param key      - A task distribution key
     * @param callable - A task that will be executed on the selected worker by key
     * @param <T>      - A generic type that specifies type of return value
     * @return {@link  java.util.concurrent.CompletableFuture}
     */
    default <T> CompletableFuture<T> execute(String key, Callable<T> callable) {
        return execute(key, callable, Collections.emptyMap());
    }


    /**
     * Executes task and returns CompletableFuture
     *
     * @param key        - A task distribution key
     * @param callable   - A task that will be executed on the selected worker by key
     * @param <T>        - A generic type that specifies type of return value
     * @param attributes - Additional task attributes
     * @return {@link  java.util.concurrent.CompletableFuture}
     */
    <T> CompletableFuture<T> execute(String key, Callable<T> callable, Map<String, Object> attributes);

    /**
     * Returns a real count of workers(Threads)
     *
     * @return {@link java.lang.Integer}
     */
    int getWorkerCount();

    /**
     * Returns the sum of nodes of all the workers
     *
     * @return {@link java.lang.Integer}
     */
    int getWorkerNodeCount();

    /**
     * Makes dead the worker service
     */
    void shutdown();

}
