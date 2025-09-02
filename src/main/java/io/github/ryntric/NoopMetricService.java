package io.github.ryntric;

@Deprecated
final class NoopMetricService implements MetricService {
    private NoopMetricService() {}


    public static NoopMetricService getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public MetricContext newMetricContext(String workerName, String taskName) {
        return null;
    }

    @Override
    public void incrementTaskCount(MetricName metricName, MetricContext context) {

    }

    @Override
    public MetricTimerContext startTimer(MetricName metricName, MetricContext context) {
        return null;
    }

    @Override
    public void stopTimer(MetricTimerContext context) {

    }

    @Override
    public void recordLatency(MetricName metricName, long latency, MetricContext context) {

    }


    private static class InstanceHolder {
        private static final NoopMetricService INSTANCE = new NoopMetricService();
    }
}
