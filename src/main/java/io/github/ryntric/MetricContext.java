package io.github.ryntric;

final class MetricContext {
    private final String workerName;
    private final String taskName;

    MetricContext(String workerName, String taskName) {
        this.workerName = workerName;
        this.taskName = taskName;
    }

    public String getWorkerName() {
        return workerName;
    }

    public String getTaskName() {
        return taskName;
    }

}
