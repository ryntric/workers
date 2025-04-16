package io.github.viacheslavbondarchuk;

import java.util.concurrent.atomic.AtomicInteger;

class WorkerFactory {
    private static final String NAME_TEMPLATE = "%s-worker-%d-%d";
    private static final String THREAD_GROUP_TEMPLATE = "%s-group-%d";

    private static final AtomicInteger workerPool = new AtomicInteger();
    private static final AtomicInteger workerGroup = new AtomicInteger();

    private static int count = 0;

    private WorkerFactory() {}

    private int getAndIncrement() {
        return count++;
    }

    private static String getWorkerGroupName(String name) {
        return String.format(THREAD_GROUP_TEMPLATE, name, workerGroup.getAndIncrement());
    }

    public static WorkerFactory getNewWorkerFactory() {
        return new WorkerFactory();
    }

    private String getWorkerName(String name, int poolId) {
        return String.format(NAME_TEMPLATE, name, poolId, getAndIncrement());
    }

    public Worker[] create(String name, int workerCount, OnWorkerTaskCompletion listener) {
        int poolId = workerPool.getAndIncrement();
        String workerGroupName = getWorkerGroupName(name);
        ThreadGroup workerGroup = new ThreadGroup(workerGroupName);

        Worker[] workers = new Worker[workerCount];
        for (int i = 0; i < workerCount; i++) {
            workers[i] = new Worker(getWorkerName(name, poolId), workerGroup, listener);
        }
        return workers;
    }

}
