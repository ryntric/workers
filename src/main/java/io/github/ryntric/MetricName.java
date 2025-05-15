package io.github.ryntric;

enum MetricName {
    WORKER_TASK_EXECUTION_TIME("worker_task_execution_time"),
    WORKER_FINISHED_TASKS_COUNT("worker_finished_tasks_count"),
    WORKER_EXECUTION_TIME_LATENCY("worker_execution_time_latency");

    private final String value;

    MetricName(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
