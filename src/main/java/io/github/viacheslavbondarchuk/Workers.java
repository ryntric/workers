package io.github.viacheslavbondarchuk;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class Workers {
    private static final Runtime RUNTIME = Runtime.getRuntime();
    private static final Integer AVAILABLE_PROCESSORS = RUNTIME.availableProcessors();

    private Workers() {}

    public static int getCores() {
        return AVAILABLE_PROCESSORS > 1 ? AVAILABLE_PROCESSORS - 1 : AVAILABLE_PROCESSORS;
    }

    /**
     * Creates a new worker service
     * Parameters:
     */
    public static WorkerService newWorkerService(String name, int workers, HashFunction hfunc) {
        return new WorkerServiceImpl(name, workers, hfunc);
    }

    /**
     * Creates a new worker service
     */
    public static WorkerService newWorkerService(String name, HashFunction hfunc, OnWorkerTaskCompletion onTaskCompletion) {
        return new WorkerServiceImpl(name, Workers.getCores(), 1, hfunc, onTaskCompletion);
    }

    /**
     * Creates a new worker service
     */
    public static WorkerService newWorkerService(String name, int replicas, HashFunction hfunc, OnWorkerTaskCompletion onTaskCompletion) {
        return new WorkerServiceImpl(name, Workers.getCores(), replicas, hfunc, onTaskCompletion);
    }

    /**
     * Creates a new worker service
     */
    public static WorkerService newWorkerService(String name, int workers, int replicas, HashFunction hfunc,
            OnWorkerTaskCompletion onTaskCompletion) {
        return new WorkerServiceImpl(name, workers, replicas, hfunc, onTaskCompletion);
    }

    /**
     * Returns a hash code of the key
     */
    public static long getKeyHash(String key, HashFunction hasher, Charset charset) {
        HashCode hashCode = hasher.hashString(key, charset);
        return hashCode.bits() > 32 ? hashCode.asLong() : hashCode.asInt();
    }

    /**
     * Returns a hash code of key
     */
    public static long getKeyHash(String key, HashFunction hasher) {
        return Workers.getKeyHash(key, hasher, StandardCharsets.UTF_8);
    }

    /**
     * Handles an interrupt exception
     */
    public static void handleInterruptException(InterruptedException ignored) {
        Thread currentThread = Thread.currentThread();
        currentThread.interrupt();
    }

    /**
     * Returns current time in milliseconds
     *
     * @return {@link java.lang.Long}
     */
    public static long getCurrentTimeInMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Extracts and casts attribute to the generic type
     *
     * @param attributes - Task attributes
     * @param key        - An attribute key
     * @param <T>        - A generic type of the attribute
     * @return {@link T}
     */
    public static <T> T getTaskAttribute(Map<String, Object> attributes, String key) {
        return (T) attributes.get(key);
    }
}
