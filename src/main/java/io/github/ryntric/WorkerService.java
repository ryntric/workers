package io.github.ryntric;

import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

public abstract class WorkerService implements AutoCloseable, Closeable {

    abstract long getWorkerNodeId(Long keyHashCode);

    abstract WorkerNode getWorkerNode(Long keyHashCode);

    public abstract String getName();

    /**
     * @param key       task distribution key
     * @param task  task that will be executed on the selected worker by key
     * @return @return {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract CompletableFuture<Void> execute(String key, RunnableTask task);

    /**
     * @param key       task distribution key
     * @param task  task that will be executed on the selected worker by key
     * @return @return {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract CompletableFuture<Void> execute(Integer key, RunnableTask task);

    /**
     * @param key       task distribution key
     * @param task  task that will be executed on the selected worker by key
     * @return @return {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract CompletableFuture<Void> execute(Long key, RunnableTask task);

    /**
     * @param key       task distribution key
     * @param task  task that will be executed on the selected worker by key
     * @return @return {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract CompletableFuture<Void> execute(byte[] key, RunnableTask task);

    /**
     * Executes task and returns CompletableFuture
     * @param key       task distribution key
     * @param task  task that will be executed on the selected worker by key
     * @param <T>       generic type that specifies type of return value
     * @return  {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract <T> CompletableFuture<T> execute(String key, CallableTask<T> task);

    /**
     * Executes task and returns CompletableFuture
     * @param key       task distribution key
     * @param task  task that will be executed on the selected worker by key
     * @param <T>       generic type that specifies type of return value
     * @return  {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract <T> CompletableFuture<T> execute(Integer key, CallableTask<T> task);

    /**
     * Executes task and returns CompletableFuture
     * @param key       task distribution key
     * @param callable  task that will be executed on the selected worker by key
     * @param <T>       generic type that specifies type of return value
     * @return  {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract <T> CompletableFuture<T> execute(Long key, CallableTask<T> callable);

    /**
     * Executes task and returns CompletableFuture
     * @param key       task distribution key
     * @param task  task that will be executed on the selected worker by key
     * @param <T>       generic type that specifies type of return value
     * @return  {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract <T> CompletableFuture<T> execute(byte[] key, CallableTask<T> task);

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
