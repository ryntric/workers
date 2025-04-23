package io.github.viacheslavbondarchuk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class WorkerFactory {
    private static final String NAME_TEMPLATE = "%s-worker-%d-%d";
    private static final String THREAD_GROUP_TEMPLATE = "%s-group-%d";

    private static final AtomicInteger workerPool = new AtomicInteger();
    private static final AtomicInteger workerGroup = new AtomicInteger();

    private WorkerFactory() {}

    private static String getWorkerGroupName(String name) {
        return String.format(THREAD_GROUP_TEMPLATE, name, workerGroup.getAndIncrement());
    }

    private static String getWorkerName(String name, int poolId, int workerId) {
        return String.format(NAME_TEMPLATE, name, poolId, workerId);
    }

    public static List<Worker> createWorkers(String name, int workerCount, MetricService metrics) {
        int poolId = workerPool.getAndIncrement();
        String workerGroupName = getWorkerGroupName(name);
        ThreadGroup workerGroup = new ThreadGroup(workerGroupName);

        List<Worker> workers = new ArrayList<>(workerCount);
        for (int i = 0; i < workerCount; i++) {
            workers.add(new Worker(getWorkerName(name, poolId, i), workerGroup, metrics));
        }
        return workers;
    }

}
