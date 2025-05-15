package io.github.ryntric;

final class NoopMetricService implements MetricService {
    private NoopMetricService() {}


    public static NoopMetricService getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void incrementTaskCount(MetricName name, CompletionTaskStatus status, MetricContext context) {

    }

    @Override
    public MetricTimerContext startTimer(MetricName name, MetricContext context) {
        return null;
    }

    @Override
    public void stopTimer(MetricTimerContext context) {

    }

    @Override
    public void recordLatency(MetricName name, long latency, MetricContext context) {

    }

    private static class InstanceHolder {
        private static final NoopMetricService INSTANCE = new NoopMetricService();
    }
}
