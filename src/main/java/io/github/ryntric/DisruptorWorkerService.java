package io.github.ryntric;

import com.google.common.hash.HashFunction;
import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

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
    private final String name;
    private final int workerCount;
    private final int replicaCount;
    private final int bufferSize;
    private final HashFunction hashFunction;
    private final ProducerType producerType;
    private final AtomicBoolean alive;

    public DisruptorWorkerService(WorkerServiceConfig config) {
        checkConfig(config);
        this.workers = new ArrayList<>(config.getWorkerCount());
        this.nodes = new TreeMap<>();
        this.alive = new AtomicBoolean(true);
        this.name = config.getName();
        this.workerCount = config.getWorkerCount();
        this.replicaCount = config.getReplicaCount();
        this.bufferSize = config.getBufferSize();
        this.hashFunction = config.getHashFunction();
        this.producerType = config.getProducerType();
        this.init(config);
    }

    private void init(WorkerServiceConfig config) {
        MetricService metrics = MetricServiceFactory.getMetricService(name, config.getMetricConfig());
        WorkerThreadFactory threadFactory = new WorkerThreadFactory(name);
        DisruptorFactory df = new DisruptorFactory(threadFactory, metrics, config.getWaitStrategy(), producerType, config.getBufferSize());

        for (int i = 0; i < workerCount; i++) {
            Disruptor<WorkerTaskEvent> disruptor = df.create();
            disruptor.start();
            workers.add(disruptor);
        }

        WorkerNodeFactory.createAndConsume(workers, replicaCount, node -> nodes.put(WorkerUtil.getKeyHash(node.getName(), hashFunction), node));
    }

    private void checkConfig(WorkerServiceConfig config) {
        assert config.getWorkerCount() > 0 : "workerCount must be greater than 0";
        assert config.getReplicaCount() > 0 : "replicaCount must be greater than 0";
        assert config.getHashFunction() != null : "hashFunction must not be null";
        assert config.getName() != null : "name must not be null";
        assert config.getWaitStrategy() != null : "waitStrategy must not be null";
        assert config.getProducerType() != null : "producerType must not be null";
        assert Integer.bitCount(config.getBufferSize()) == 1 : "bufferSize must be a power of 2";
    }

    private void checkLiveness() {
        if (!alive.get()) {
            throw new WorkerServiceDeadException("WorkerService is dead");
        }
    }

    private void checkTask(AbstractWorkerTask<?> task) {
        if (task == null) {
            throw new IllegalArgumentException("task must not be null");
        }
    }

    private <T> CompletableFuture<T> _execute(Long keyHashCode, AbstractWorkerTask<T> task) {
        checkTask(task);
        checkLiveness();
        WorkerNode node = getWorkerNode(keyHashCode);
        CompletableFuture<T> future = new CompletableFuture<>();
        node.execute(task, future);
        return future;
    }

    private <T> CompletableFuture<T> _tryExecute(Long keyHashCode, AbstractWorkerTask<T> task) throws InsufficientCapacityException {
        checkTask(task);
        checkLiveness();
        WorkerNode node = getWorkerNode(keyHashCode);
        CompletableFuture<T> future = new CompletableFuture<>();
        node.tryExecute(task, future);
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
        return name;
    }

    @Override
    public int getBufferSize() {
        return bufferSize;
    }

    @Override
    public ProducerType getProducerType() {
        return producerType;
    }

    @Override
    public CompletableFuture<Void> execute(String key, RunnableTask task) {
        return _execute(WorkerUtil.getKeyHash(key, hashFunction), task);
    }

    @Override
    public CompletableFuture<Void> execute(Integer key, RunnableTask task) {
        return _execute(WorkerUtil.getKeyHash(key, hashFunction), task);
    }

    @Override
    public CompletableFuture<Void> execute(Long key, RunnableTask task) {
        return _execute(WorkerUtil.getKeyHash(key, hashFunction), task);
    }

    @Override
    public CompletableFuture<Void> execute(byte[] key, RunnableTask task) {
        return _execute(WorkerUtil.getKeyHash(key, hashFunction), task);
    }

    @Override
    public CompletableFuture<Void> tryExecute(String key, RunnableTask task) throws InsufficientCapacityException {
        return _tryExecute(WorkerUtil.getKeyHash(key, hashFunction), task);
    }

    @Override
    public CompletableFuture<Void> tryExecute(Integer key, RunnableTask task) throws InsufficientCapacityException {
        return _tryExecute(WorkerUtil.getKeyHash(key, hashFunction), task);
    }

    @Override
    public CompletableFuture<Void> tryExecute(Long key, RunnableTask task) throws InsufficientCapacityException {
        return _tryExecute(WorkerUtil.getKeyHash(key, hashFunction), task);
    }

    @Override
    public CompletableFuture<Void> tryExecute(byte[] key, RunnableTask task) throws InsufficientCapacityException {
        return _tryExecute(WorkerUtil.getKeyHash(key, hashFunction), task);
    }

    @Override
    public <T> CompletableFuture<T> execute(String key, CallableTask<T> task) {
        return _execute(WorkerUtil.getKeyHash(key, hashFunction), task);
    }

    @Override
    public <T> CompletableFuture<T> execute(Integer key, CallableTask<T> task) {
        return _execute(WorkerUtil.getKeyHash(key, hashFunction), task);
    }

    @Override
    public <T> CompletableFuture<T> execute(Long key, CallableTask<T> task) {
        return _execute(WorkerUtil.getKeyHash(key, hashFunction), task);
    }

    @Override
    public <T> CompletableFuture<T> execute(byte[] key, CallableTask<T> task) {
        return _execute(WorkerUtil.getKeyHash(key, hashFunction), task);
    }

    @Override
    public <T> CompletableFuture<T> tryExecute(String key, CallableTask<T> task) throws InsufficientCapacityException {
        return _tryExecute(WorkerUtil.getKeyHash(key, hashFunction), task);
    }

    @Override
    public <T> CompletableFuture<T> tryExecute(Integer key, CallableTask<T> task) throws InsufficientCapacityException {
        return _tryExecute(WorkerUtil.getKeyHash(key, hashFunction), task);
    }

    @Override
    public <T> CompletableFuture<T> tryExecute(Long key, CallableTask<T> task) throws InsufficientCapacityException {
        return _tryExecute(WorkerUtil.getKeyHash(key, hashFunction), task);
    }

    @Override
    public <T> CompletableFuture<T> tryExecute(byte[] key, CallableTask<T> task) throws InsufficientCapacityException {
        return _tryExecute(WorkerUtil.getKeyHash(key, hashFunction), task);
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
            workers.forEach(Disruptor::shutdown);
        }
    }

    @Override
    public void close() {
        shutdown();
    }
}
