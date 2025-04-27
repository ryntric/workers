package io.github.viacheslavbondarchuk;

import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.ThreadFactory;

import static com.lmax.disruptor.dsl.ProducerType.MULTI;

final class DisruptorFactory {
    private final ThreadFactory threadFactory;
    private final MetricService metrics;
    private final WaitStrategy waitStrategy;
    private final int bufferSize;

    DisruptorFactory(ThreadFactory threadFactory, MetricService metrics, WaitStrategy waitStrategy, int bufferSize) {
        this.threadFactory = threadFactory;
        this.metrics = metrics;
        this.waitStrategy = waitStrategy;
        this.bufferSize = bufferSize;
    }

    Disruptor<WorkerTaskEvent> create() {
        Disruptor<WorkerTaskEvent> disruptor = new Disruptor<>(WorkerTaskEventFactory.getInstance(), bufferSize, threadFactory, MULTI, waitStrategy);
        disruptor.handleEventsWith(new WorkerTaskEventHandler(metrics));
        return disruptor;
    }
}
