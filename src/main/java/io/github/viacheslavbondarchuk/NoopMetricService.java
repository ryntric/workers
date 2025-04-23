package io.github.viacheslavbondarchuk;

import java.util.function.ToDoubleFunction;

final class NoopMetricService implements MetricService {
    private NoopMetricService() {}


    public static NoopMetricService getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void incrementTaskCount(String name, String workerName, String key, TaskStatus status) {

    }

    @Override
    public void startTimer(String name, String workerName, String key) {

    }

    @Override
    public void stopTimer(String key) {

    }

    @Override
    public <T> void gauge(String name, String workerName, T state, ToDoubleFunction<T> function) {

    }

    private static class InstanceHolder {
        private static final NoopMetricService INSTANCE = new NoopMetricService();
    }
}
