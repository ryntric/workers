package io.github.viacheslavbondarchuk;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;

final class MicrometerMetricService implements MetricService {
    private final Map<String, MetricTimerContext> metricTimerContexts = new ConcurrentHashMap<>();

    private final MeterRegistry registry;
    private final String workerServiceName;

    MicrometerMetricService(MeterRegistry registry, String workerServiceName) {
        this.registry = registry;
        this.workerServiceName = workerServiceName;
    }

    private Tags getTags(MetricContext context) {
        Tags tags = Tags.of(WORKER_NAME, context.getWorkerName(), WORKER_TASK_KEY, context.getTaskKey(), WORKER_SERVICE_NAME, workerServiceName);
        if (context.getTaskName() != null) {
            tags.and(WORKER_TASK_NAME, context.getTaskName());
        }
        return tags;
    }

    @Override
    public void incrementTaskCount(String name, CompletionTaskStatus status, MetricContext context) {
        Tags tags = getTags(context).and(WORKER_TASK_COMPLETION_STATUS, status.name());
        Counter counter = registry.counter(name, tags);
        counter.increment();
    }

    @Override
    public void startTimer(String name, MetricContext context) {
        Timer.Sample sample = Timer.start(registry);
        Tags tags = getTags(context);
        MetricTimerContext timerContext = new MetricTimerContext(name, tags, sample);
        metricTimerContexts.put(context.getTaskKey(), timerContext);
    }

    @Override
    public void stopTimer(MetricContext context) {
        MetricTimerContext timerContext = metricTimerContexts.remove(context.getTaskKey());
        if (timerContext != null) {
            timerContext.stop(registry);
        }
    }

    @Override
    public void recordLatency(String name, long latencyNs, MetricContext context) {
        Timer.builder(name)
                .tags(getTags(context))
                .register(registry)
                .record(latencyNs, TimeUnit.NANOSECONDS);
    }

    @Override
    public <T> void gauge(String name, T state, ToDoubleFunction<T> function, MetricContext context) {
        Tags tags = Tags.of(WORKER_NAME, context.getWorkerName(), WORKER_SERVICE_NAME, workerServiceName);
        registry.gauge(name, tags, state, function);
    }


}
