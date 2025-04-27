package io.github.viacheslavbondarchuk;

import java.io.Closeable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public abstract class WorkerService implements AutoCloseable, Closeable {

    abstract long getWorkerNodeId(String key);

    abstract WorkerNode getWorkerNode(String key);

    public abstract String getName();

    /**
     * Executes task and returns CompletableFuture
     * @param key      task distribution key
     * @param runnable task that will be executed on the selected worker by key
     * @return {@link  java.util.concurrent.CompletableFuture}
     */
    public CompletableFuture<Void> execute(String key, Runnable runnable) {
        return execute(key, null, runnable);
    }

    /**
     * @param key       task distribution key
     * @param taskName  task name is used for metrics
     * @param runnable  task that will be executed on the selected worker by key
     * @return @return {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract CompletableFuture<Void> execute(String key, String taskName, Runnable runnable);

    /**
     * Executes task and returns CompletableFuture
     * @param key      task distribution key
     * @param callable task that will be executed on the selected worker by key
     * @param <T>      generic type that specifies type of return value
     * @return {@link  java.util.concurrent.CompletableFuture}
     */
    public <T> CompletableFuture<T> execute(String key, Callable<T> callable) {
        return execute(key, null, callable);
    }

    /**
     * Executes task and returns CompletableFuture
     * @param key       task distribution key
     * @param taskName  task name is used for metrics
     * @param callable  task that will be executed on the selected worker by key
     * @param <T>       generic type that specifies type of return value
     * @return  {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract <T> CompletableFuture<T> execute(String key, String taskName, Callable<T> callable);

    /**
     * Returns a real count of workers(Threads)
     * @return {@link java.lang.Integer}
     */
    public abstract int getWorkerCount();

    /**
     * Returns the worker node count per worker
     * @return {@link java.lang.Integer}
     */
    public abstract int getWorkerNodeCount();

    /**
     * Makes dead the worker service
     */
    public abstract void shutdown();

}
