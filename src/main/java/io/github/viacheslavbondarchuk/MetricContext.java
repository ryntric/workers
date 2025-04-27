package io.github.viacheslavbondarchuk;

final class MetricContext {
    private final String workerName;
    private final String taskKey;
    private final String taskName;

    MetricContext(String workerName, String taskKey, String taskName) {
        this.workerName = workerName;
        this.taskKey = taskKey;
        this.taskName = taskName;
    }

    public String getWorkerName() {
        return workerName;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public String getTaskName() {
        return taskName;
    }

}
