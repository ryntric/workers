package io.github.viacheslavbondarchuk;

final class WorkerNode {
    private final String name;
    private final Worker worker;

    WorkerNode(String name, Worker worker) {
        this.name = name;
        this.worker = worker;
    }

    void execute(WorkerTask<?> workerTask) {
        worker.execute(workerTask);
    }

    String getName() {
        return name;
    }

    String getWorkerName() {
        return worker.getName();
    }


}
