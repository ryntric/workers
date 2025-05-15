package io.github.ryntric;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;

final class MetricTimerContext {
    private final MetricName metricName;
    private final Tags tags;
    private final Timer.Sample sample;
    private final MeterRegistry registry;

    MetricTimerContext(MetricName metricName, Tags tags, Timer.Sample sample, MeterRegistry registry) {
        this.metricName = metricName;
        this.tags = tags;
        this.sample = sample;
        this.registry = registry;
    }

    void stop() {
        sample.stop(registry.timer(metricName.value(), tags));
    }

}
