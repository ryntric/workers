package io.github.viacheslavbondarchuk;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import static io.github.viacheslavbondarchuk.WorkerTaskCompletionStatus.CANCELLED;
import static io.github.viacheslavbondarchuk.WorkerTaskCompletionStatus.ERROR;
import static io.github.viacheslavbondarchuk.WorkerTaskCompletionStatus.SUCCESS;

@SuppressWarnings({"rawtypes", "unchecked"})
final class Worker extends Thread {
    private final AtomicBoolean alive = new AtomicBoolean(true);
    private final AtomicBoolean parked = new AtomicBoolean(false);
    private final Queue<WorkerTask<?>> queue = new ConcurrentLinkedQueue<>();
    private final OnWorkerTaskCompletion listener;

    Worker(String name, ThreadGroup group, OnWorkerTaskCompletion listener) {
        super(group, name);
        this.listener = listener;
        start();
    }

    private void park() {
        if (parked.compareAndSet(false, true)) {
            LockSupport.park();
        }
    }

    private void unpark() {
        if (parked.compareAndSet(true, false)) {
            LockSupport.unpark(this);
        }
    }

    void shutdown() {
        if (alive.compareAndSet(true, false)) {
            unpark();
        }
    }

    void execute(WorkerTask<?> task) {
        assert alive.get() : "Worker is dead";
        queue.offer(task);
        unpark();
    }

    private void onComplete(String key, Exception ex, WorkerTaskCompletionStatus status, Map<String, Object> attributes) {
        if (listener != null) {
            switch (status) {
                case SUCCESS:
                    listener.onSuccess(key, getName(), attributes);
                    break;
                case CANCELLED:
                    listener.onCancel(key, getName(), attributes);
                    break;
                case ERROR:
                    listener.onError(key, getName(), ex, attributes);
                    break;
            }
        }
    }

    private void _execute(WorkerTask task) {
        CompletableFuture future = task.getCompletableFuture();
        if (!future.isCancelled()) {
            try {
                future.complete(task.execute());
                onComplete(task.getKey(), null, SUCCESS, task.getAttributes());
            } catch (InterruptedException ie) {
                Workers.handleInterruptException(ie);
                future.completeExceptionally(ie);
                onComplete(task.getKey(), ie, ERROR, task.getAttributes());
            } catch (Exception ex) {
                future.completeExceptionally(ex);
                onComplete(task.getKey(), ex, ERROR, task.getAttributes());
            }
        } else {
            onComplete(task.getKey(), null, CANCELLED, task.getAttributes());
        }
    }

    @Override
    public void run() {
        while (alive.get()) {
            WorkerTask<?> task = queue.poll();
            if (task != null) {
                _execute(task);
                continue;
            }
            park();
        }
    }
}
