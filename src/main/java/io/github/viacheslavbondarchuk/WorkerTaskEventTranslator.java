package io.github.viacheslavbondarchuk;

import com.lmax.disruptor.EventTranslatorOneArg;

final class WorkerTaskEventTranslator implements EventTranslatorOneArg<WorkerTaskEvent, WorkerTask<?>> {

    private WorkerTaskEventTranslator() {}

    @Override
    public void translateTo(WorkerTaskEvent event, long sequence, WorkerTask<?> task) {
        event.setWorkerTask(task);
    }

    public static WorkerTaskEventTranslator getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final WorkerTaskEventTranslator INSTANCE = new WorkerTaskEventTranslator();
    }
}
