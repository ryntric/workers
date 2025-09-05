package io.github.ryntric;

import io.github.ryntric.EventTranslator.EventTranslatorFiveArg;
import io.github.ryntric.EventTranslator.EventTranslatorFourArg;
import io.github.ryntric.EventTranslator.EventTranslatorOneArg;
import io.github.ryntric.EventTranslator.EventTranslatorThreeArg;
import io.github.ryntric.EventTranslator.EventTranslatorTwoArg;
import io.github.ryntric.util.WorkerUtil;

/**
 * A high-performance event processing service that manages a group of workers
 * backed by ring buffers.
 * <p>
 * Events are published to workers based on the hash of a routing key
 * (e.g., user ID, partition key, or byte array), ensuring that events with the same
 * key are consistently routed to the same worker.
 * <p>
 * Each worker processes events independently using the provided
 * {@link EventHandler}, and can be configured with various wait strategies,
 * buffer sizes, and sequencing options via {@link WorkerServiceConfig}.
 *
 * @param <T> the event type handled by this service
 */

@SuppressWarnings("unchecked")
public final class WorkerService<T> {
    private final String name;
    private final Worker<T>[] workers;
    private final WorkerNode<T>[] nodes;
    private final HashCodeProvider hashCodeProvider;
    private final int workerCount;
    private final int replicaCount;
    private final int nodeCount;
    private final int mask;
    private final PaddedBoolean isAlive = new PaddedBoolean();


    /**
     * Creates a new {@code WorkerService} instance with the given configuration.
     *
     * @param name              name prefix for worker threads
     * @param handler           the event handler that processes events
     * @param factory           the factory for creating new event instances
     * @param hashCodeProvider  the provider used to hash routing keys
     * @param config            worker service configuration
     */
    public WorkerService(
            String name,
            EventHandler<T> handler,
            EventFactory<T> factory,
            HashCodeProvider hashCodeProvider,
            WorkerServiceConfig config) {
        this.name = name;
        this.workerCount = config.getWorkerCount();
        this.replicaCount = config.getReplicaCount();
        this.nodeCount = WorkerUtil.ceilPowerOfTwo(workerCount * replicaCount);
        this.hashCodeProvider = hashCodeProvider;
        this.workers = new Worker[workerCount];
        this.nodes = new WorkerNode[nodeCount];
        this.mask = nodeCount - 1;
        this.init(name, config.getBufferSize(), handler, factory, config);
    }

    private void init(String name, int bufferSize, EventHandler<T> handler, EventFactory<T> factory, WorkerServiceConfig config) {
        RingBufferFactory<T> ringBufferFactory = new RingBufferFactory<>(bufferSize, config.getProducerWaitPolicy(), config.getSequencerType(), factory);
        WorkerFactory<T> workerFactory = new WorkerFactory<>(name, config.getConsumerWaitPolicy(), handler, config.getBatchSizeLimit(), ringBufferFactory);

        for (int i = 0; i < workerCount; i++) {
            workers[i] = workerFactory.newWorker();
        }

        for (int i = 0; i < nodeCount; i++) {
            nodes[i] = new WorkerNode<>(workers[i % workerCount]);
        }
    }

    private WorkerNode<T> selectNode(int hashcode) {
        return nodes[hashcode & mask];
    }

    /**
     * Starts all workers.
     * Workers will begin consuming events from their ring buffers.
     */
    public void start() {
        if (isAlive.compareAndSetVolatile(false, true)) {
            for (Worker<T> worker : workers) worker.start();
        }
    }

    /**
     * Shuts down all workers gracefully.
     * Workers will stop consuming events and release resources.
     */
    public void shutdown() {
        if (isAlive.compareAndSetVolatile(true, false)) {
            for (Worker<T> worker : workers) worker.shutdown();
        }
    }

    private void checkLiveness() {
        if (!isAlive.getAcquire()) throw new WorkerServiceTerminatedException(name);
    }

    private <A> void _publishEvent(int hashcode, EventTranslatorOneArg<T, A> translator, A arg) {
        checkLiveness();
        WorkerNode<T> node = selectNode(hashcode);
        node.publishEvent(translator, arg);
    }

    private <A> void _publishEvents(int hashcode, EventTranslatorOneArg<T, A> translator, A[] args) {
        checkLiveness();
        WorkerNode<T> node = selectNode(hashcode);
        node.publishEvents(translator, args);
    }

    private <A, B> void _publishEvent(int hashcode, EventTranslatorTwoArg<T, A, B> translator, A arg0, B arg1) {
        checkLiveness();
        WorkerNode<T> node = selectNode(hashcode);
        node.publishEvent(translator, arg0, arg1);
    }

    private <A, B> void _publishEvents(int hashcode, EventTranslatorTwoArg<T, A, B> translator, A[] arg0, B[] arg1) {
        checkLiveness();
        WorkerNode<T> node = selectNode(hashcode);
        node.publishEvents(translator, arg0, arg1);
    }

    private <A, B, C> void _publishEvent(int hashcode, EventTranslatorThreeArg<T, A, B, C> translator, A arg0, B arg1, C arg2) {
        checkLiveness();
        WorkerNode<T> node = selectNode(hashcode);
        node.publishEvent(translator, arg0, arg1, arg2);
    }

    private <A, B, C> void _publishEvents(int hashcode, EventTranslatorThreeArg<T, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2) {
        checkLiveness();
        WorkerNode<T> node = selectNode(hashcode);
        node.publishEvents(translator, arg0, arg1, arg2);
    }

    private <A, B, C, D> void _publishEvent(int hashcode, EventTranslatorFourArg<T, A, B, C, D> translator, A arg0, B arg1, C arg2, D arg3) {
        checkLiveness();
        WorkerNode<T> node = selectNode(hashcode);
        node.publishEvent(translator, arg0, arg1, arg2, arg3);
    }

    private <A, B, C, D> void _publishEvents(int hashcode, EventTranslatorFourArg<T, A, B, C, D> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3) {
        checkLiveness();
        WorkerNode<T> node = selectNode(hashcode);
        node.publishEvents(translator, arg0, arg1, arg2, arg3);
    }

    private <A, B, C, D, E> void _publishEvent(int hashcode, EventTranslatorFiveArg<T, A, B, C, D, E> translator, A arg0, B arg1, C arg2, D arg3, E arg4) {
        checkLiveness();
        WorkerNode<T> node = selectNode(hashcode);
        node.publishEvent(translator, arg0, arg1, arg2, arg3, arg4);
    }

    private <A, B, C, D, E> void _publishEvents(int hashcode, EventTranslatorFiveArg<T, A, B, C, D, E> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3, E[] arg4) {
        checkLiveness();
        WorkerNode<T> node = selectNode(hashcode);
        node.publishEvents(translator, arg0, arg1, arg2, arg3, arg4);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg        argument passed to the translator
     * @param <A>        type of the argument
     */
    public <A> void publishEvent(String key, EventTranslatorOneArg<T, A> translator, A arg) {
        _publishEvent(hashCodeProvider.provide(key),  translator, arg);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg        argument passed to the translator
     * @param <A>        type of the argument
     */
    public <A> void publishEvent(int key, EventTranslatorOneArg<T, A> translator, A arg) {
        _publishEvent(hashCodeProvider.provide(key),  translator, arg);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg        argument passed to the translator
     * @param <A>        type of the argument
     */
    public <A> void publishEvent(long key, EventTranslatorOneArg<T, A> translator, A arg) {
        _publishEvent(hashCodeProvider.provide(key),  translator, arg);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg        argument passed to the translator
     * @param <A>        type of the argument
     */
    public <A> void publishEvent(byte[] key, EventTranslatorOneArg<T, A> translator, A arg) {
        _publishEvent(hashCodeProvider.provide(key),  translator, arg);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param args       arguments passed to the translator
     * @param <A>        type of the argument
     */
    public <A> void publishEvents(String key, EventTranslatorOneArg<T, A> translator, A[] args) {
        _publishEvents(hashCodeProvider.provide(key),  translator, args);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param args       arguments passed to the translator
     * @param <A>        type of the argument
     */
    public <A> void publishEvents(int key, EventTranslatorOneArg<T, A> translator, A[] args) {
        _publishEvents(hashCodeProvider.provide(key),  translator, args);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param args       arguments passed to the translator
     * @param <A>        type of the argument
     */
    public <A> void publishEvents(long key, EventTranslatorOneArg<T, A> translator, A[] args) {
        _publishEvents(hashCodeProvider.provide(key),  translator, args);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param args       arguments passed to the translator
     * @param <A>        type of the argument
     */
    public <A> void publishEvents(byte[] key, EventTranslatorOneArg<T, A> translator, A[] args) {
        _publishEvents(hashCodeProvider.provide(key),  translator, args);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st argument passed to the translator
     * @param arg1       2nd argument passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     */
    public <A, B> void publishEvent(String key, EventTranslatorTwoArg<T, A, B> translator, A arg0, B arg1) {
        _publishEvent(hashCodeProvider.provide(key),  translator, arg0, arg1);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st argument passed to the translator
     * @param arg1       2nd argument passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     */
    public <A, B> void publishEvent(int key, EventTranslatorTwoArg<T, A, B> translator, A arg0, B arg1) {
        _publishEvent(hashCodeProvider.provide(key),  translator, arg0, arg1);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st argument passed to the translator
     * @param arg1       2nd argument passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     */
    public <A, B> void publishEvent(long key, EventTranslatorTwoArg<T, A, B> translator, A arg0, B arg1) {
        _publishEvent(hashCodeProvider.provide(key),  translator, arg0, arg1);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st argument passed to the translator
     * @param arg1       2nd argument passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     */
    public <A, B> void publishEvent(byte[] key, EventTranslatorTwoArg<T, A, B> translator, A arg0, B arg1) {
        _publishEvent(hashCodeProvider.provide(key),  translator, arg0, arg1);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st arguments passed to the translator
     * @param arg1       2nd arguments passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     */
    public <A, B> void publishEvents(String key, EventTranslatorTwoArg<T, A, B> translator, A[] arg0, B[] arg1) {
        _publishEvents(hashCodeProvider.provide(key),  translator, arg0, arg1);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st arguments passed to the translator
     * @param arg1       2nd arguments passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     */
    public <A, B> void publishEvents(int key, EventTranslatorTwoArg<T, A, B> translator, A[] arg0, B[] arg1) {
        _publishEvents(hashCodeProvider.provide(key),  translator, arg0, arg1);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st arguments passed to the translator
     * @param arg1       2nd arguments passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     */
    public <A, B> void publishEvents(long key, EventTranslatorTwoArg<T, A, B> translator, A[] arg0, B[] arg1) {
        _publishEvents(hashCodeProvider.provide(key),  translator, arg0, arg1);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st arguments passed to the translator
     * @param arg1       2nd arguments passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     */
    public <A, B> void publishEvents(byte[] key, EventTranslatorTwoArg<T, A, B> translator, A[] arg0, B[] arg1) {
        _publishEvents(hashCodeProvider.provide(key),  translator, arg0, arg1);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st argument passed to the translator
     * @param arg1       2nd argument passed to the translator
     * @param arg2       3rd argument passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     */
    public <A, B, C> void publishEvent(String key, EventTranslatorThreeArg<T, A, B, C> translator, A arg0, B arg1, C arg2) {
        _publishEvent(hashCodeProvider.provide(key),  translator, arg0, arg1, arg2);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st argument passed to the translator
     * @param arg1       2nd argument passed to the translator
     * @param arg2       3rd argument passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     */
    public <A, B, C> void publishEvent(int key, EventTranslatorThreeArg<T, A, B, C> translator, A arg0, B arg1, C arg2) {
        _publishEvent(hashCodeProvider.provide(key),  translator, arg0, arg1, arg2);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st argument passed to the translator
     * @param arg1       2nd argument passed to the translator
     * @param arg2       3rd argument passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     */
    public <A, B, C> void publishEvent(long key, EventTranslatorThreeArg<T, A, B, C> translator, A arg0, B arg1, C arg2) {
        _publishEvent(hashCodeProvider.provide(key),  translator, arg0, arg1, arg2);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st argument passed to the translator
     * @param arg1       2nd argument passed to the translator
     * @param arg2       3rd argument passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     */
    public <A, B, C> void publishEvent(byte[] key, EventTranslatorThreeArg<T, A, B, C> translator, A arg0, B arg1, C arg2) {
        _publishEvent(hashCodeProvider.provide(key),  translator, arg0, arg1, arg2);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st arguments passed to the translator
     * @param arg1       2nd arguments passed to the translator
     * @param arg2       3rd arguments passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     */
    public <A, B, C> void publishEvents(String key, EventTranslatorThreeArg<T, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2) {
        _publishEvents(hashCodeProvider.provide(key),  translator, arg0, arg1, arg2);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st arguments passed to the translator
     * @param arg1       2nd arguments passed to the translator
     * @param arg2       3rd arguments passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     */
    public <A, B, C> void publishEvents(int key, EventTranslatorThreeArg<T, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2) {
        _publishEvents(hashCodeProvider.provide(key),  translator, arg0, arg1, arg2);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st arguments passed to the translator
     * @param arg1       2nd arguments passed to the translator
     * @param arg2       3rd arguments passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     */
    public <A, B, C> void publishEvents(long key, EventTranslatorThreeArg<T, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2) {
        _publishEvents(hashCodeProvider.provide(key),  translator, arg0, arg1, arg2);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st arguments passed to the translator
     * @param arg1       2nd arguments passed to the translator
     * @param arg2       3rd arguments passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     */
    public <A, B, C> void publishEvents(byte[] key, EventTranslatorThreeArg<T, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2) {
        _publishEvents(hashCodeProvider.provide(key),  translator, arg0, arg1, arg2);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st argument passed to the translator
     * @param arg1       2nd argument passed to the translator
     * @param arg2       3rd argument passed to the translator
     * @param arg3       4th argument passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     * @param <D>        type of the 4th argument
     */
    public <A, B, C, D> void publishEvent(String key, EventTranslatorFourArg<T, A, B, C, D> translator, A arg0, B arg1, C arg2, D arg3) {
        WorkerNode<T> node = selectNode(hashCodeProvider.provide(key));
        node.publishEvent(translator, arg0, arg1, arg2, arg3);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st argument passed to the translator
     * @param arg1       2nd argument passed to the translator
     * @param arg2       3rd argument passed to the translator
     * @param arg3       4th argument passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     * @param <D>        type of the 4th argument
     */
    public <A, B, C, D> void publishEvent(int key, EventTranslatorFourArg<T, A, B, C, D> translator, A arg0, B arg1, C arg2, D arg3) {
        _publishEvent(hashCodeProvider.provide(key), translator, arg0, arg1, arg2, arg3);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st argument passed to the translator
     * @param arg1       2nd argument passed to the translator
     * @param arg2       3rd argument passed to the translator
     * @param arg3       4th argument passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     * @param <D>        type of the 4th argument
     */
    public <A, B, C, D> void publishEvent(long key, EventTranslatorFourArg<T, A, B, C, D> translator, A arg0, B arg1, C arg2, D arg3) {
        _publishEvent(hashCodeProvider.provide(key), translator, arg0, arg1, arg2, arg3);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st argument passed to the translator
     * @param arg1       2nd argument passed to the translator
     * @param arg2       3rd argument passed to the translator
     * @param arg3       4th argument passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     * @param <D>        type of the 4th argument
     */
    public <A, B, C, D> void publishEvent(byte[] key, EventTranslatorFourArg<T, A, B, C, D> translator, A arg0, B arg1, C arg2, D arg3) {
        _publishEvent(hashCodeProvider.provide(key), translator, arg0, arg1, arg2, arg3);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st arguments passed to the translator
     * @param arg1       2nd arguments passed to the translator
     * @param arg2       3rd arguments passed to the translator
     * @param arg3       4th arguments passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     * @param <D>        type of the 4th argument
     */
    public <A, B, C, D> void publishEvents(String key, EventTranslatorFourArg<T, A, B, C, D> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3) {
        _publishEvents(hashCodeProvider.provide(key), translator, arg0, arg1, arg2, arg3);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st arguments passed to the translator
     * @param arg1       2nd arguments passed to the translator
     * @param arg2       3rd arguments passed to the translator
     * @param arg3       4th arguments passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     * @param <D>        type of the 4th argument
     */
    public <A, B, C, D> void publishEvents(int key, EventTranslatorFourArg<T, A, B, C, D> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3) {
        _publishEvents(hashCodeProvider.provide(key), translator, arg0, arg1, arg2, arg3);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st arguments passed to the translator
     * @param arg1       2nd arguments passed to the translator
     * @param arg2       3rd arguments passed to the translator
     * @param arg3       4th arguments passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     * @param <D>        type of the 4th argument
     */
    public <A, B, C, D> void publishEvents(long key, EventTranslatorFourArg<T, A, B, C, D> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3) {
        _publishEvents(hashCodeProvider.provide(key), translator, arg0, arg1, arg2, arg3);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st arguments passed to the translator
     * @param arg1       2nd arguments passed to the translator
     * @param arg2       3rd arguments passed to the translator
     * @param arg3       4th arguments passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     * @param <D>        type of the 4th argument
     */
    public <A, B, C, D> void publishEvents(byte[] key, EventTranslatorFourArg<T, A, B, C, D> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3) {
        _publishEvents(hashCodeProvider.provide(key), translator, arg0, arg1, arg2, arg3);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st argument passed to the translator
     * @param arg1       2nd argument passed to the translator
     * @param arg2       3rd argument passed to the translator
     * @param arg3       4th argument passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     * @param <D>        type of the 4th argument
     */
    public <A, B, C, D, E> void publishEvent(String key, EventTranslatorFiveArg<T, A, B, C, D, E> translator, A arg0, B arg1, C arg2, D arg3, E arg4) {
        _publishEvent(hashCodeProvider.provide(key), translator, arg0, arg1, arg2, arg3, arg4);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st argument passed to the translator
     * @param arg1       2nd argument passed to the translator
     * @param arg2       3rd argument passed to the translator
     * @param arg3       4th argument passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     * @param <D>        type of the 4th argument
     * @param <E>        type of the 5th argument
     */
    public <A, B, C, D, E> void publishEvent(int key, EventTranslatorFiveArg<T, A, B, C, D, E> translator, A arg0, B arg1, C arg2, D arg3, E arg4) {
        _publishEvent(hashCodeProvider.provide(key), translator, arg0, arg1, arg2, arg3, arg4);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st argument passed to the translator
     * @param arg1       2nd argument passed to the translator
     * @param arg2       3rd argument passed to the translator
     * @param arg3       4th argument passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     * @param <D>        type of the 4th argument
     * @param <E>        type of the 5th argument
     */
    public <A, B, C, D, E> void publishEvent(long key, EventTranslatorFiveArg<T, A, B, C, D, E> translator, A arg0, B arg1, C arg2, D arg3, E arg4) {
        _publishEvent(hashCodeProvider.provide(key), translator, arg0, arg1, arg2, arg3, arg4);
    }

    /**
     * Publishes a single event to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st argument passed to the translator
     * @param arg1       2nd argument passed to the translator
     * @param arg2       3rd argument passed to the translator
     * @param arg3       4th argument passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     * @param <D>        type of the 4th argument
     * @param <E>        type of the 5th argument
     */
    public <A, B, C, D, E> void publishEvent(byte[] key, EventTranslatorFiveArg<T, A, B, C, D, E> translator, A arg0, B arg1, C arg2, D arg3, E arg4) {
        _publishEvent(hashCodeProvider.provide(key), translator, arg0, arg1, arg2, arg3, arg4);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st arguments passed to the translator
     * @param arg1       2nd arguments passed to the translator
     * @param arg2       3rd arguments passed to the translator
     * @param arg3       4th arguments passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     * @param <D>        type of the 4th argument
     * @param <E>        type of the 5th argument
     */
    public <A, B, C, D, E> void publishEvent(String key, EventTranslatorFiveArg<T, A, B, C, D, E> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3, E[] arg4) {
        _publishEvents(hashCodeProvider.provide(key), translator, arg0, arg1, arg2, arg3, arg4);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st arguments passed to the translator
     * @param arg1       2nd arguments passed to the translator
     * @param arg2       3rd arguments passed to the translator
     * @param arg3       4th arguments passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     * @param <D>        type of the 4th argument
     * @param <E>        type of the 5th argument
     */
    public <A, B, C, D, E> void publishEvent(int key, EventTranslatorFiveArg<T, A, B, C, D, E> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3, E[] arg4) {
        _publishEvents(hashCodeProvider.provide(key), translator, arg0, arg1, arg2, arg3, arg4);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st arguments passed to the translator
     * @param arg1       2nd arguments passed to the translator
     * @param arg2       3rd arguments passed to the translator
     * @param arg3       4th arguments passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     * @param <D>        type of the 4th argument
     * @param <E>        type of the 5th argument
     */
    public <A, B, C, D, E> void publishEvent(long key, EventTranslatorFiveArg<T, A, B, C, D, E> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3, E[] arg4) {
        _publishEvents(hashCodeProvider.provide(key), translator, arg0, arg1, arg2, arg3, arg4);
    }

    /**
     * Publishes an event batch to the worker determined by the hash of the given key.
     *
     * @param key        routing key used for consistent worker selection
     * @param translator translator used to populate the event with data
     * @param arg0       1st arguments passed to the translator
     * @param arg1       2nd arguments passed to the translator
     * @param arg2       3rd arguments passed to the translator
     * @param arg3       4th arguments passed to the translator
     * @param <A>        type of the 1st argument
     * @param <B>        type of the 2nd argument
     * @param <C>        type of the 3rd argument
     * @param <D>        type of the 4th argument
     * @param <E>        type of the 5th argument
     */
    public <A, B, C, D, E> void publishEvent(byte[] key, EventTranslatorFiveArg<T, A, B, C, D, E> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3, E[] arg4) {
        _publishEvents(hashCodeProvider.provide(key), translator, arg0, arg1, arg2, arg3, arg4);
    }

}
