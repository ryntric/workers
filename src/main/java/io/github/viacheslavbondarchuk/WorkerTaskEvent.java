package io.github.viacheslavbondarchuk;

class WorkerTaskEvent {
    private WorkerTask<?> workerTask;

    WorkerTask<?> getWorkerTask() {
        return workerTask;
    }

    void setWorkerTask(WorkerTask<?> workerTask) {
        this.workerTask = workerTask;
    }
}
