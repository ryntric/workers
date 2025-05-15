package io.github.ryntric;

import java.text.MessageFormat;
import java.util.concurrent.ThreadFactory;

final class WorkerThreadFactory implements ThreadFactory {
    private static final String WORKER_THREAD_NAME_TEMPLATE = "{0}-worker-{1}";
    private static final String WORKER_THREAD_GROUP_NAME_TEMPLATE = "{0}-worker-tg-{1}";

    private int counter;

    private final String workerServiceName;

    public WorkerThreadFactory(String workerServiceName) {
        this.workerServiceName = workerServiceName;
    }

    private int getNextId() {
        return counter++;
    }

    private String getName(int id) {
        return MessageFormat.format(WORKER_THREAD_NAME_TEMPLATE, workerServiceName, id);
    }

    private String getGroupName(int id) {
        return MessageFormat.format(WORKER_THREAD_GROUP_NAME_TEMPLATE, workerServiceName, id);
    }

    @Override
    public Thread newThread(Runnable runnable) {
        int nextId = getNextId();
        ThreadGroup threadGroup = new ThreadGroup(getGroupName(nextId));
        return new Thread(threadGroup, runnable, getName(nextId), nextId);
    }
}
