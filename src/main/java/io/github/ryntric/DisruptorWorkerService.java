package io.github.ryntric;

import com.lmax.disruptor.dsl.Disruptor;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public final class DisruptorWorkerService extends WorkerService {
    private final List<Disruptor<WorkerTaskEvent>> workers;
    private final NavigableMap<Long, WorkerNode> nodes;
    private final WorkerServiceConfig config;
    private final AtomicBoolean alive;

    public DisruptorWorkerService(WorkerServiceConfig config) {
        checkConfig(config);
        this.workers = new ArrayList<>(config.getWorkerCount());
        this.nodes = new TreeMap<>();
        this.alive = new AtomicBoolean(true);
        this.config = config;
        this.init();
    }

    private void init() {
        MetricService metrics = MetricServiceFactory.getMetricService(config.getName(), config.getMetricConfig());
        WorkerThreadFactory threadFactory = new WorkerThreadFactory(config.getName());
        DisruptorFactory df = new DisruptorFactory(threadFactory, metrics, config.getWaitStrategy(), config.getBufferSize());

        for (int i = 0; i < config.getWorkerCount(); i++) {
            Disruptor<WorkerTaskEvent> disruptor = df.create();
            disruptor.start();
            workers.add(disruptor);
        }

        WorkerNodeFactory.createAndConsume(workers, config.getReplicaCount(), node -> nodes.put(WorkerUtil.getKeyHash(node.getName(),
                config.getHashFunction()), node));
    }

    private void checkConfig(WorkerServiceConfig config) {
        assert config.getWorkerCount() > 0 : "workerCount must be greater than 0";
        assert config.getReplicaCount() > 0 : "replicaCount must be greater than 0";
        assert config.getHashFunction() != null : "hashFunction must not be null";
        assert config.getName() != null : "name must not be null";
        assert config.getWaitStrategy() != null : "waitStrategy must not be null";
        assert Integer.bitCount(config.getBufferSize()) == 1 : "bufferSize must be a power of 2";
    }

    private void checkLiveness() {
        if (!alive.get()) {
            throw new WorkerServiceDeadException("WorkerService is dead");
        }
    }

    private CompletableFuture<Void> _execute(Long keyHashCode, RunnableTask task) {
        checkLiveness();
        WorkerNode node = getWorkerNode(keyHashCode);
        CompletableFuture<Void> future = new CompletableFuture<>();
        node.execute(task, future);
        return future;
    }

    private <T> CompletableFuture<T> _execute(Long keyHashCode, CallableTask<T> task) {
        checkLiveness();
        WorkerNode node = getWorkerNode(keyHashCode);
        CompletableFuture<T> future = new CompletableFuture<>();
        node.execute(task, future);
        return future;
    }

    @Override
    long getWorkerNodeId(Long keyHashCode) {
        SortedMap<Long, WorkerNode> tailed = nodes.tailMap(keyHashCode);
        return tailed.isEmpty() ? nodes.firstKey() : tailed.firstKey();
    }

    @Override
    WorkerNode getWorkerNode(Long keyHashCode) {
        long workerNodeId = getWorkerNodeId(keyHashCode);
        return nodes.get(workerNodeId);
    }

    public String getName() {
        return config.getName();
    }

    @Override
    public CompletableFuture<Void> execute(String key, RunnableTask task) {
        return _execute(WorkerUtil.getKeyHash(key, config.getHashFunction()), task);
    }

    @Override
    public CompletableFuture<Void> execute(Integer key, RunnableTask task) {
        return _execute(WorkerUtil.getKeyHash(key, config.getHashFunction()), task);
    }

    @Override
    public CompletableFuture<Void> execute(Long key, RunnableTask task) {
        return _execute(WorkerUtil.getKeyHash(key, config.getHashFunction()), task);
    }

    @Override
    public CompletableFuture<Void> execute(byte[] key, RunnableTask task) {
        return _execute(WorkerUtil.getKeyHash(key, config.getHashFunction()), task);
    }

    @Override
    public <T> CompletableFuture<T> execute(String key, CallableTask<T> task) {
        return _execute(WorkerUtil.getKeyHash(key, config.getHashFunction()), task);
    }

    @Override
    public <T> CompletableFuture<T> execute(Integer key, CallableTask<T> task) {
        return _execute(WorkerUtil.getKeyHash(key, config.getHashFunction()), task);
    }

    @Override
    public <T> CompletableFuture<T> execute(Long key, CallableTask<T> task) {
        return _execute(WorkerUtil.getKeyHash(key, config.getHashFunction()), task);
    }

    @Override
    public <T> CompletableFuture<T> execute(byte[] key, CallableTask<T> task) {
        return _execute(WorkerUtil.getKeyHash(key, config.getHashFunction()), task);
    }

    @Override
    public int getWorkerCount() {
        return config.getWorkerCount();
    }

    @Override
    public int getWorkerNodeCount() {
        return config.getReplicaCount();
    }

    @Override
    public void shutdown() {
        if (alive.compareAndSet(true, false)) {
            workers.forEach(Disruptor::shutdown);
        }
    }

    @Override
    public void close() {
        shutdown();
    }
}
