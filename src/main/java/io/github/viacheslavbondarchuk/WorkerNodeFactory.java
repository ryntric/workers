package io.github.viacheslavbondarchuk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class WorkerNodeFactory {
    private static final String WORKER_NODE_NAME_TEMPLATE = "%s-vwn-%d";

    private WorkerNodeFactory() {}

    public static Collection<WorkerNode> createWorkerNodes(List<Worker> workers, int quantity) {
        ArrayList<WorkerNode> nodes = new ArrayList<>(quantity);
        int workerNodeIdx = 0;
        for (Worker worker : workers) {
            for (int i = 0; i < quantity; i++) {
                nodes.add(new WorkerNode(String.format(WORKER_NODE_NAME_TEMPLATE, worker.getName(), workerNodeIdx++), worker));
            }
        }
        return nodes;
    }

}
