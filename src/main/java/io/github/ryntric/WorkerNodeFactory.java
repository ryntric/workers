package io.github.ryntric;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.List;
import java.util.function.Consumer;

final class WorkerNodeFactory {
    private static final String WORKER_NODE_NAME_TEMPLATE = "%s-vwn-%d";


    private WorkerNodeFactory() {}

    public static void createAndConsume(List<Disruptor<WorkerTaskEvent>> workers, int quantity, Consumer<WorkerNode> consumer) {
        RandomBasedGenerator generator = Generators.randomBasedGenerator();
        int idx = 0;
        for (Disruptor<WorkerTaskEvent> worker : workers) {
            for (int i = 0; i < quantity; i++) {
                consumer.accept(new WorkerNode(String.format(WORKER_NODE_NAME_TEMPLATE, generator.generate(), idx++), worker));
            }
        }
    }

}
