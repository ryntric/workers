package io.github.ryntric;

enum MetricName {
    WORKER_TASK_EXECUTION_TIME_MS("worker_task_execution_time_ms"),
    WORKER_FINISHED_TASKS_COUNT("worker_finished_tasks_count"),
    WORKER_EXECUTION_TIME_LATENCY_MS("worker_execution_time_latency_ms");

    private final String value;

    MetricName(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
