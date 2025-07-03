package io.github.ryntric;

import io.micrometer.core.instrument.Tags;

final class MetricContext {
    private final MetricTagCompositor compositor;

    MetricContext(String workerServiceName, String workerName, String taskName, MetricConfig config) {
        this.compositor = new MetricTagCompositor(config);
        compositor.add(MetricTagName.WORKER_SERVICE_NAME, workerServiceName);
        compositor.add(MetricTagName.WORKER_NAME, workerName);
        compositor.add(MetricTagName.WORKER_TASK_NAME, taskName);
    }

    public Tags getTags() {
        return compositor.tags();
    }

}
