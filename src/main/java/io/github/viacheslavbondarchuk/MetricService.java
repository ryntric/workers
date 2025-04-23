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

    void incrementTaskCount(String name, String workerName, String key, TaskStatus status);

    void startTimer(String name, String workerName, String key);

    void stopTimer(String key);

    <T> void gauge(String name, String workerName, T state, ToDoubleFunction<T> function);

}
