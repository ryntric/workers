package io.github.viacheslavbondarchuk;

import com.google.common.base.Preconditions;
import com.google.common.hash.HashFunction;

import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

final class WorkerServiceImpl implements WorkerService {
    private final NavigableMap<Long, WorkerNode> replicas;
    private final Worker[] workers;
    private final HashFunction hasher;
    private final int workerCount;
    private final int replicaCount;
    private final AtomicBoolean alive;

    WorkerServiceImpl(String name, int workerCount, HashFunction hasher) {
        this(name, workerCount, 1, hasher, null);
    }

    WorkerServiceImpl(String name, int workerCount, int replicaCount, HashFunction hasher, OnWorkerTaskCompletion listener) {
        Preconditions.checkArgument(replicaCount > 0, "replicaCount must be greater than 0");
        Preconditions.checkArgument(workerCount > 0, "workerCount must be greater than 0");
        Preconditions.checkNotNull(name, "Worker name should not be null");
        Preconditions.checkNotNull(hasher, "Hasher should not be null");

        this.workers = WorkerFactory.getNewWorkerFactory().create(name, workerCount, listener);
        this.replicas = new TreeMap<>();
        this.hasher = hasher;
        this.workerCount = workerCount;
        this.replicaCount = replicaCount;
        this.alive = new AtomicBoolean(true);
        this.init();
    }

    private void init() {
        WorkerNodeFactory.getNewWorkerNodeFactory().create(workers, replicaCount)
                .forEach(node -> replicas.put(Workers.getKeyHash(node.getName(), hasher), node));
    }

    private long getWorkerNodeId(String key) {
        SortedMap<Long, WorkerNode> tailed = replicas.tailMap(Workers.getKeyHash(key, hasher));
        return tailed.isEmpty() ? replicas.firstKey() : tailed.firstKey();
    }

    private WorkerNode getWorkerNode(String key) {
        long workerNodeId = getWorkerNodeId(key);
        return replicas.get(workerNodeId);
    }

    private void checkLiveness() {
        if (!alive.get()) {
            throw new RuntimeException("Worker service is dead");
        }
    }

    @Override
    public CompletableFuture<Void> execute(String key, Runnable runnable, Map<String, Object> attributes) {
        checkLiveness();
        WorkerNode node = getWorkerNode(key);
        CompletableFuture<Void> future = new CompletableFuture<>();
        node.execute(new VoidTask(key, future, runnable, attributes));
        return future;
    }

    @Override
    public <T> CompletableFuture<T> execute(String key, Callable<T> callable, Map<String, Object> attributes) {
        checkLiveness();
        WorkerNode node = getWorkerNode(key);
        CompletableFuture<T> future = new CompletableFuture<>();
        node.execute(new CallableTask<>(key, future, callable, attributes));
        return future;
    }

    @Override
    public int getWorkerCount() {
        return workerCount;
    }

    @Override
    public int getWorkerNodeCount() {
        return workerCount * replicaCount;
    }

    @Override
    public void shutdown() {
        if (alive.compareAndSet(true, false)) {
            for (Worker worker : workers) {
                worker.shutdown();
            }
        }
    }

    @Override
    public void close() {
        shutdown();
    }
}
