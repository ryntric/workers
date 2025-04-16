package io.github.viacheslavbondarchuk;

import java.util.ArrayList;
import java.util.Collection;

class WorkerNodeFactory {
    private static final String WORKER_NODE_NAME_TEMPLATE = "%s-vwn-%d";

    private int count = 0;

    private WorkerNodeFactory() {}

    private int getAndIncrement() {
        return count++;
    }

    public Collection<WorkerNode> create(Worker[] workers, int quantity) {
        ArrayList<WorkerNode> nodes = new ArrayList<>(quantity);
        for (Worker worker : workers) {
            for (int i = 0; i < quantity; i++) {
                nodes.add(new WorkerNode(String.format(WORKER_NODE_NAME_TEMPLATE, worker.getName(), getAndIncrement()), worker));
            }
        }
        return nodes;
    }

    public static WorkerNodeFactory getNewWorkerNodeFactory() {
        return new WorkerNodeFactory();
    }

}
