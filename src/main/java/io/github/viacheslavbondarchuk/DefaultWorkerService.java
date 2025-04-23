package io.github.viacheslavbondarchuk;

import com.google.common.hash.HashFunction;

import java.util.List;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DefaultWorkerService implements WorkerService {
    private final NavigableMap<Long, WorkerNode> nodes = new TreeMap<>();
    private final AtomicBoolean alive = new AtomicBoolean(true);

    private final List<Worker> workers;
    private final HashFunction hashFunction;
    private final int workerCount;
    private final int replicaCount;
    private final String name;

    public DefaultWorkerService(WorkerServiceConfig config) {
        checkConfig(config);
        this.hashFunction = config.getHashFunction();
        this.workerCount = config.getWorkerCount();
        this.replicaCount = config.getReplicaCount();
        this.name = config.getName();
        this.workers = WorkerFactory.createWorkers(name, workerCount,
                MetricServiceFactory.getMetricService(config.getMeterRegistry(), config.getName()));
        this.init();
    }

    private void checkConfig(WorkerServiceConfig config) {
        assert config.getWorkerCount() > 0 : "workerCount must be greater than 0";
        assert config.getReplicaCount() > 0 : "replicaCount must be greater than 0";
        assert config.getHashFunction() != null : "hashFunction must not be null";
        assert config.getName() != null : "name must not be null";
    }

    private void init() {
        WorkerNodeFactory.createWorkerNodes(workers, replicaCount)
                .forEach(node -> nodes.put(WorkerUtil.getKeyHash(node.getName(), hashFunction), node));
    }

    private long getWorkerNodeId(String key) {
        SortedMap<Long, WorkerNode> tailed = nodes.tailMap(WorkerUtil.getKeyHash(key, hashFunction));
        return tailed.isEmpty() ? nodes.firstKey() : tailed.firstKey();
    }

    private WorkerNode getWorkerNode(String key) {
        long workerNodeId = getWorkerNodeId(key);
        return nodes.get(workerNodeId);
    }

    private void checkLiveness() {
        if (!alive.get()) throw new RuntimeException("Worker service is dead");
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public CompletableFuture<Void> execute(String key, Runnable runnable) {
        checkLiveness();
        WorkerNode node = getWorkerNode(key);
        CompletableFuture<Void> future = new CompletableFuture<>();
        node.execute(new VoidTask(key, future, runnable));
        return future;
    }

    @Override
    public <T> CompletableFuture<T> execute(String key, Callable<T> callable) {
        checkLiveness();
        WorkerNode node = getWorkerNode(key);
        CompletableFuture<T> future = new CompletableFuture<>();
        node.execute(new CallableTask<>(key, future, callable));
        return future;
    }

    @Override
    public int getWorkerCount() {
        return workerCount;
    }

    @Override
    public int getWorkerNodeCount() {
        return replicaCount;
    }

    @Override
    public void shutdown() {
        if (alive.compareAndSet(true, false)) {
            workers.forEach(Worker::shutdown);
        }
    }

    @Override
    public void close() {
        shutdown();
    }
}
