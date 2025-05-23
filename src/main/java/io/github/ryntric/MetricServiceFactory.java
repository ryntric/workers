package io.github.ryntric;

final class MetricServiceFactory {
    private MetricServiceFactory() {}

    static MetricService getMetricService(String workerServiceName, MetricConfig config) {
        return config.getMeterRegistry() == null ? NoopMetricService.getInstance() : new MicrometerMetricService(workerServiceName, config);
    }
}
