package io.github.ryntric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;

import java.util.concurrent.TimeUnit;

final class MicrometerMetricService implements MetricService {
    private final MetricConfig metricConfig;
    private final String workerServiceName;

    MicrometerMetricService(String workerServiceName, MetricConfig metricConfig) {
        this.workerServiceName = workerServiceName;
        this.metricConfig = metricConfig;
    }

    @Override
    public MetricContext newMetricContext(String workerName, String taskName) {
        return new MetricContext(workerServiceName, workerName, taskName, metricConfig);
    }

    @Override
    public void incrementTaskCount(MetricName metricName, MetricContext context) {
        Counter counter = metricConfig.getMeterRegistry()
                .counter(metricName.value(), context.getTags());
        counter.increment();
    }

    @Override
    public MetricTimerContext startTimer(MetricName metricName, MetricContext context) {
        Timer.Sample sample = Timer.start(metricConfig.getMeterRegistry());
        return new MetricTimerContext(metricName, context.getTags(), sample, metricConfig.getMeterRegistry());
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
                .timer(metricName.value(), context.getTags())
                .record(latency, TimeUnit.MILLISECONDS);
    }

}
