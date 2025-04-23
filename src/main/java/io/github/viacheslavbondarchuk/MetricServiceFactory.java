package io.github.viacheslavbondarchuk;

import io.micrometer.core.instrument.MeterRegistry;

final class MetricServiceFactory {
    private MetricServiceFactory() {}

    static MetricService getMetricService(MeterRegistry registry, String workerServiceName) {
        return registry == null ? NoopMetricService.getInstance() : new MicrometerMetricService(registry, workerServiceName);
    }
}
