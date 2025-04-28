package io.github.viacheslavbondarchuk;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static io.github.viacheslavbondarchuk.MetricTagName.WORKER_NAME;
import static io.github.viacheslavbondarchuk.MetricTagName.WORKER_SERVICE_NAME;
import static io.github.viacheslavbondarchuk.MetricTagName.WORKER_TASK_COMPLETION_STATUS;
import static io.github.viacheslavbondarchuk.MetricTagName.WORKER_TASK_KEY;
import static io.github.viacheslavbondarchuk.MetricTagName.WORKER_TASK_NAME;

final class MicrometerMetricService implements MetricService {
    private final Map<String, MetricTimerContext> metricTimerContexts = new ConcurrentHashMap<>();

    private final MetricConfig metricConfig;
    private final String workerServiceName;

    MicrometerMetricService(String workerServiceName, MetricConfig metricConfig) {
        this.workerServiceName = workerServiceName;
        this.metricConfig = metricConfig;
    }

    private MetricTagCompositor getTagCompositor(MetricContext context) {
        return new MetricTagCompositor(metricConfig)
                .add(WORKER_NAME, context.getWorkerName())
                .add(WORKER_TASK_KEY, context.getTaskKey())
                .add(WORKER_SERVICE_NAME, workerServiceName)
                .add(WORKER_TASK_NAME, context.getTaskName());
    }

    @Override
    public void incrementTaskCount(MetricName metricName, CompletionTaskStatus status, MetricContext context) {
        Tags tags = getTagCompositor(context).add(WORKER_TASK_COMPLETION_STATUS, status.name()).tags();
        Counter counter = metricConfig.getMeterRegistry().counter(metricName.value(), tags);
        counter.increment();
    }

    @Override
    public void startTimer(MetricName metricName, MetricContext context) {
        Timer.Sample sample = Timer.start(metricConfig.getMeterRegistry());
        Tags tags = getTagCompositor(context).tags();
        MetricTimerContext timerContext = new MetricTimerContext(metricName, tags, sample);
        metricTimerContexts.put(context.getTaskKey(), timerContext);
    }

    @Override
    public void stopTimer(MetricContext context) {
        MetricTimerContext timerContext = metricTimerContexts.remove(context.getTaskKey());
        if (timerContext != null) {
            timerContext.stop(metricConfig.getMeterRegistry());
        }
    }

    @Override
    public void recordLatency(MetricName metricName, long latency, MetricContext context) {
        metricConfig.getMeterRegistry()
                .timer(metricName.value(), getTagCompositor(context).tags())
                .record(latency, TimeUnit.MILLISECONDS);
    }

}
