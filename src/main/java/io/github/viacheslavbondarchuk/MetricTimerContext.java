package io.github.viacheslavbondarchuk;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;

final class MetricTimerContext {
    private final MetricName metricName;
    private final Tags tags;
    private final Timer.Sample sample;

    MetricTimerContext(MetricName metricName, Tags tags, Timer.Sample sample) {
        this.metricName = metricName;
        this.tags = tags;
        this.sample = sample;
    }

    void stop(MeterRegistry registry) {
        sample.stop(registry.timer(metricName.value(), tags));
    }

}
