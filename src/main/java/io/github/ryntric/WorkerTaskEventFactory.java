package io.github.ryntric;

import com.lmax.disruptor.EventFactory;

class WorkerTaskEventFactory implements EventFactory<WorkerTaskEvent> {

    private WorkerTaskEventFactory() {}

    @Override
    public WorkerTaskEvent newInstance() {
        return new WorkerTaskEvent();
    }

    public static WorkerTaskEventFactory getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final WorkerTaskEventFactory INSTANCE = new WorkerTaskEventFactory();
    }
}
