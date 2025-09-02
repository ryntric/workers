package io.github.ryntric;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.List;

@Deprecated
final class WorkerNodeFactory {
    private static final String WORKER_NODE_NAME_TEMPLATE = "%s-vwn-%d";


    private WorkerNodeFactory() {}

    static WorkerNode[] create(List<Disruptor<WorkerTaskEvent>> workers, int quantity) {
        RandomBasedGenerator generator = Generators.randomBasedGenerator();
        WorkerNode[] nodes = new WorkerNode[quantity * workers.size()];
        int idx = 0;
        for (int i = 0; i < quantity; i++) {
            for (Disruptor<WorkerTaskEvent> worker : workers) {
                nodes[idx++] = new WorkerNode(String.format(WORKER_NODE_NAME_TEMPLATE, generator.generate(), idx), worker.getRingBuffer());
            }
        }
        return nodes;
    }

}
