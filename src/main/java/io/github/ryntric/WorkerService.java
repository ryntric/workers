package io.github.ryntric;

import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.dsl.ProducerType;

import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

public abstract class WorkerService implements AutoCloseable, Closeable {

    /**
     * @return name of this worker service
     */
    public abstract String getName();

    /**
     * @return buffer size of {@link com.lmax.disruptor.dsl.Disruptor}
     */
    public abstract int getBufferSize();

    /**
     * @return <code>ProducerType</code> of {@link com.lmax.disruptor.dsl.Disruptor}
     */
    public abstract ProducerType getProducerType();

    /**
     * @param key  task distribution key
     * @param task task that will be executed on the selected worker by key
     * @return @return {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract CompletableFuture<Void> execute(String key, RunnableTask task);

    /**
     * @param key  task distribution key
     * @param task task that will be executed on the selected worker by key
     * @return @return {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract CompletableFuture<Void> execute(int key, RunnableTask task);

    /**
     * @param key  task distribution key
     * @param task task that will be executed on the selected worker by key
     * @return @return {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract CompletableFuture<Void> execute(long key, RunnableTask task);

    /**
     * @param key  task distribution key
     * @param task task that will be executed on the selected worker by key
     * @return @return {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract CompletableFuture<Void> execute(byte[] key, RunnableTask task);

    /**
     * @param key  task distribution key
     * @param task task that will be executed on the selected worker by key
     * @return @return {@link  java.util.concurrent.CompletableFuture}
     * @throws InsufficientCapacityException when <code>RingBuffer</code> is full
     */
    public abstract CompletableFuture<Void> tryExecute(String key, RunnableTask task) throws InsufficientCapacityException;

    /**
     * @param key  task distribution key
     * @param task task that will be executed on the selected worker by key
     * @return @return {@link  java.util.concurrent.CompletableFuture}
     * @throws InsufficientCapacityException when <code>RingBuffer</code> is full
     */
    public abstract CompletableFuture<Void> tryExecute(int key, RunnableTask task) throws InsufficientCapacityException;

    /**
     * @param key  task distribution key
     * @param task task that will be executed on the selected worker by key
     * @return @return {@link  java.util.concurrent.CompletableFuture}
     * @throws InsufficientCapacityException when <code>RingBuffer</code> is full
     */
    public abstract CompletableFuture<Void> tryExecute(long key, RunnableTask task) throws InsufficientCapacityException;

    /**
     * @param key  task distribution key
     * @param task task that will be executed on the selected worker by key
     * @return @return {@link  java.util.concurrent.CompletableFuture}
     * @throws InsufficientCapacityException when <code>RingBuffer</code> is full
     */
    public abstract CompletableFuture<Void> tryExecute(byte[] key, RunnableTask task) throws InsufficientCapacityException;

    /**
     * Executes task and returns CompletableFuture
     *
     * @param key  task distribution key
     * @param task task that will be executed on the selected worker by key
     * @param <T>  generic type that specifies type of return value
     * @return {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract <T> CompletableFuture<T> execute(String key, CallableTask<T> task);

    /**
     * Executes task and returns CompletableFuture
     *
     * @param key  task distribution key
     * @param task task that will be executed on the selected worker by key
     * @param <T>  generic type that specifies type of return value
     * @return {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract <T> CompletableFuture<T> execute(int key, CallableTask<T> task);

    /**
     * Executes task and returns CompletableFuture
     *
     * @param key      task distribution key
     * @param callable task that will be executed on the selected worker by key
     * @param <T>      generic type that specifies type of return value
     * @return {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract <T> CompletableFuture<T> execute(long key, CallableTask<T> callable);

    /**
     * Executes task and returns CompletableFuture
     *
     * @param key  task distribution key
     * @param task task that will be executed on the selected worker by key
     * @param <T>  generic type that specifies type of return value
     * @return {@link  java.util.concurrent.CompletableFuture}
     */
    public abstract <T> CompletableFuture<T> execute(byte[] key, CallableTask<T> task);

    /**
     * Executes task and returns CompletableFuture
     *
     * @param key  task distribution key
     * @param task task that will be executed on the selected worker by key
     * @param <T>  generic type that specifies type of return value
     * @return {@link  java.util.concurrent.CompletableFuture}
     * @throws InsufficientCapacityException when <code>RingBuffer</code> is full
     */
    public abstract <T> CompletableFuture<T> tryExecute(String key, CallableTask<T> task) throws InsufficientCapacityException;

    /**
     * Executes task and returns CompletableFuture
     *
     * @param key  task distribution key
     * @param task task that will be executed on the selected worker by key
     * @param <T>  generic type that specifies type of return value
     * @return {@link  java.util.concurrent.CompletableFuture}
     * @throws InsufficientCapacityException when <code>RingBuffer</code> is full
     */
    public abstract <T> CompletableFuture<T> tryExecute(int key, CallableTask<T> task) throws InsufficientCapacityException;

    /**
     * Executes task and returns CompletableFuture
     *
     * @param key  task distribution key
     * @param task task that will be executed on the selected worker by key
     * @param <T>  generic type that specifies type of return value
     * @return {@link  java.util.concurrent.CompletableFuture}
     * @throws InsufficientCapacityException when <code>RingBuffer</code> is full
     */
    public abstract <T> CompletableFuture<T> tryExecute(long key, CallableTask<T> task) throws InsufficientCapacityException;

    /**
     * Executes task and returns CompletableFuture
     *
     * @param key  task distribution key
     * @param task task that will be executed on the selected worker by key
     * @param <T>  generic type that specifies type of return value
     * @return {@link  java.util.concurrent.CompletableFuture}
     * @throws InsufficientCapacityException when <code>RingBuffer</code> is full
     */
    public abstract <T> CompletableFuture<T> tryExecute(byte[] key, CallableTask<T> task) throws InsufficientCapacityException;

    /**
     * Returns a real count of workers(Threads)
     *
     * @return {@link java.lang.Integer}
     */
    public abstract int getWorkerCount();

    /**
     * Returns the worker node count per worker
     *
     * @return {@link java.lang.Integer}
     */
    public abstract int getWorkerNodeCount();

    /**
     * Makes dead the worker service
     */
    public abstract void shutdown();

}
