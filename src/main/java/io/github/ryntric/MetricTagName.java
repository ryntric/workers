package io.github.ryntric;

public enum MetricTagName {
    WORKER_NAME("worker_name", (byte) 0x1),
    WORKER_TASK_COMPLETION_STATUS("worker_task_completion_status", (byte) (1 << 1)),
    WORKER_SERVICE_NAME("worker_service_name", (byte) (1 << 2)),
    WORKER_TASK_NAME("worker_task_name", (byte) (1 << 3));

    private final String value;
    private final byte flag;

    MetricTagName(String value, byte flag) {
        this.value = value;
        this.flag = flag;
    }

    public String value() {
        return value;
    }

    public byte flag() {
        return flag;
    }
}
