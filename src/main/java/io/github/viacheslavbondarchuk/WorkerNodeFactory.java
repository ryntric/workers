package io.github.viacheslavbondarchuk;

import com.lmax.disruptor.dsl.Disruptor;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

final class WorkerNodeFactory {
    private static final String WORKER_NODE_NAME_TEMPLATE = "%s-vwn-%d";

    private WorkerNodeFactory() {}

    public static void createAndConsume(List<Disruptor<WorkerTaskEvent>> workers, int quantity, Consumer<WorkerNode> consumer) {
        int idx = 0;
        for (Disruptor<WorkerTaskEvent> worker : workers) {
            for (int i = 0; i < quantity; i++) {
                consumer.accept(new WorkerNode(String.format(WORKER_NODE_NAME_TEMPLATE, UUID.randomUUID(), idx++), worker));
            }
        }
    }

}
