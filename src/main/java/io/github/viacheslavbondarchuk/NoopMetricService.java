package io.github.viacheslavbondarchuk;

import java.util.function.ToDoubleFunction;

final class NoopMetricService implements MetricService {
    private NoopMetricService() {}


    public static NoopMetricService getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void incrementTaskCount(String name, CompletionTaskStatus status, MetricContext context) {

    }

    @Override
    public void startTimer(String name, MetricContext context) {

    }

    @Override
    public void stopTimer(MetricContext context) {

    }

    @Override
    public void recordLatency(String name, long latencyNs, MetricContext context) {

    }

    @Override
    public <T> void gauge(String name, T state, ToDoubleFunction<T> function, MetricContext context) {

    }


    private static class InstanceHolder {
        private static final NoopMetricService INSTANCE = new NoopMetricService();
    }
}
