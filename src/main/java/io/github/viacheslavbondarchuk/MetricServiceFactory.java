package io.github.viacheslavbondarchuk;

final class MetricServiceFactory {
    private MetricServiceFactory() {}

    static MetricService getMetricService(WorkerServiceConfig config) {
        return config.getMeterRegistry() ==
                null ? NoopMetricService.getInstance() : new MicrometerMetricService(config.getMeterRegistry(), config.getName());
    }
}
