package io.github.ryntric;

import com.lmax.disruptor.EventFactory;

class WorkerTaskEventFactory implements EventFactory<WorkerTaskEvent> {
    public static final WorkerTaskEventFactory INSTANCE = new WorkerTaskEventFactory();

    private WorkerTaskEventFactory() {}

    @Override
    public WorkerTaskEvent newInstance() {
        return new WorkerTaskEvent();
    }
}
