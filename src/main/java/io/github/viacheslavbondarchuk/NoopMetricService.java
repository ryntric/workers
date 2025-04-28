package io.github.viacheslavbondarchuk;

final class NoopMetricService implements MetricService {
    private NoopMetricService() {}


    public static NoopMetricService getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void incrementTaskCount(MetricName name, CompletionTaskStatus status, MetricContext context) {

    }

    @Override
    public void startTimer(MetricName name, MetricContext context) {

    }

    @Override
    public void stopTimer(MetricContext context) {

    }

    @Override
    public void recordLatency(MetricName name, long latency, MetricContext context) {

    }

    private static class InstanceHolder {
        private static final NoopMetricService INSTANCE = new NoopMetricService();
    }
}
