package io.github.viacheslavbondarchuk;

public enum MetricTagName {
    WORKER_NAME("worker_name", (byte) 0b00000001),
    WORKER_TASK_KEY("worker_task_key", (byte) 0b00000010),
    WORKER_TASK_COMPLETION_STATUS("worker_task_completion_status", (byte) 0b00000100),
    WORKER_SERVICE_NAME("worker_service_name", (byte) 0b00001000),
    WORKER_TASK_NAME("worker_task_name", (byte) 0b00010000);

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
