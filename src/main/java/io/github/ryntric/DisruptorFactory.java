package io.github.ryntric;

import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ThreadFactory;

@Deprecated
final class DisruptorFactory {
    private final ThreadFactory threadFactory;
    private final MetricService metrics;
    private final WaitStrategy waitStrategy;
    private final ProducerType producerType;
    private final int bufferSize;

    DisruptorFactory(ThreadFactory threadFactory, MetricService metrics, WaitStrategy waitStrategy, ProducerType producerType, int bufferSize) {
        this.threadFactory = threadFactory;
        this.metrics = metrics;
        this.waitStrategy = waitStrategy;
        this.bufferSize = bufferSize;
        this.producerType = producerType;
    }

    Disruptor<WorkerTaskEvent> create() {
        Disruptor<WorkerTaskEvent> disruptor = new Disruptor<>(WorkerTaskEventFactory.INSTANCE, bufferSize, threadFactory, producerType, waitStrategy);
        disruptor.handleEventsWith(new WorkerTaskEventHandler(metrics));
        return disruptor;
    }
}
