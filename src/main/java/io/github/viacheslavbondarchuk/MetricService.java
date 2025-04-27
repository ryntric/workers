package io.github.viacheslavbondarchuk;

import java.util.function.ToDoubleFunction;

interface MetricService {

    String WORKER_NAME = "worker_name";
    String WORKER_TASK_KEY = "worker_task_key";
    String WORKER_TASK_EXECUTION_TIME = "worker_task_execution_time";
    String WORKER_TASK_COMPLETION_STATUS = "worker_task_completion_status";
    String WORKER_FINISHED_TASKS_COUNT = "worker_finished_tasks_count";
    String WORKER_TASK_QUEUE_SIZE = "worker_task_queue_size";
    String WORKER_SERVICE_NAME = "worker_service_name";
    String WORKER_TASK_NAME = "worker_task_name";
    String WORKER_EXECUTION_TIME_LATENCY = "worker_execution_task_latency";

    void incrementTaskCount(String name, CompletionTaskStatus status, MetricContext context);

    void startTimer(String name, MetricContext context);

    void stopTimer(MetricContext context);

    void recordLatency(String name, long latency, MetricContext context);

    <T> void gauge(String name, T state, ToDoubleFunction<T> function, MetricContext context);

}
