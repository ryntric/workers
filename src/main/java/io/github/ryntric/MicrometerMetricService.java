package io.github.ryntric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;

import java.util.concurrent.TimeUnit;

import static io.github.ryntric.MetricTagName.WORKER_NAME;
import static io.github.ryntric.MetricTagName.WORKER_SERVICE_NAME;
import static io.github.ryntric.MetricTagName.WORKER_TASK_COMPLETION_STATUS;
import static io.github.ryntric.MetricTagName.WORKER_TASK_NAME;

final class MicrometerMetricService implements MetricService {
    private final MetricConfig metricConfig;
    private final String workerServiceName;

    MicrometerMetricService(String workerServiceName, MetricConfig metricConfig) {
        this.workerServiceName = workerServiceName;
        this.metricConfig = metricConfig;
    }

    private MetricTagCompositor getTagCompositor(MetricContext context) {
        return new MetricTagCompositor(metricConfig)
                .add(WORKER_NAME, context.getWorkerName())
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
    public MetricTimerContext startTimer(MetricName metricName, MetricContext context) {
        Timer.Sample sample = Timer.start(metricConfig.getMeterRegistry());
        Tags tags = getTagCompositor(context).tags();
        return new MetricTimerContext(metricName, tags, sample, metricConfig.getMeterRegistry());
    }

    @Override
    public void stopTimer(MetricTimerContext context) {
        if (context != null) {
            context.stop();
        }
    }

    @Override
    public void recordLatency(MetricName metricName, long latency, MetricContext context) {
        metricConfig.getMeterRegistry()
                .timer(metricName.value(), getTagCompositor(context).tags())
                .record(latency, TimeUnit.MILLISECONDS);
    }

}
