package io.github.viacheslavbondarchuk;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.ToDoubleFunction;

final class MicrometerMetricService implements MetricService {
    private final Map<String, MetricTimerContext> metricTimerContexts = new ConcurrentHashMap<>();

    private final MeterRegistry registry;
    private final String workerServiceName;

    MicrometerMetricService(MeterRegistry registry, String workerServiceName) {
        this.registry = registry;
        this.workerServiceName = workerServiceName;
    }

    private Tags getTags(String workerName, String key) {
        return Tags.of(WORKER_NAME, workerName, WORKER_TASK_KEY, key, WORKER_SERVICE_NAME, workerServiceName);
    }

    @Override
    public void incrementTaskCount(String name, String workerName, String key, TaskStatus status) {
        Tags tags = getTags(workerName, key).and(WORKER_TASK_COMPLETION_STATUS, status.name());
        Counter counter = registry.counter(name, tags);
        counter.increment();
    }

    @Override
    public void startTimer(String name, String workerName, String key) {
        Timer.Sample sample = Timer.start(registry);
        Tags tags = getTags(workerName, key);
        MetricTimerContext context = new MetricTimerContext(name, tags, sample);
        metricTimerContexts.put(key, context);
    }

    @Override
    public void stopTimer(String key) {
        MetricTimerContext context = metricTimerContexts.remove(key);
        context.stop(registry);
    }

    @Override
    public <T> void gauge(String name, String workerName, T state, ToDoubleFunction<T> function) {
        Tags tags = Tags.of(WORKER_NAME, workerName, WORKER_SERVICE_NAME, workerServiceName);
        registry.gauge(name, tags, state, function);
    }


}
