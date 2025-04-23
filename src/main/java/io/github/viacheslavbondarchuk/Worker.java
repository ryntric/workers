package io.github.viacheslavbondarchuk;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.LockSupport;

import static io.github.viacheslavbondarchuk.MetricService.WORKER_FINISHED_TASKS_COUNT;
import static io.github.viacheslavbondarchuk.MetricService.WORKER_TASK_EXECUTION_TIME;
import static io.github.viacheslavbondarchuk.MetricService.WORKER_TASK_QUEUE_SIZE;
import static io.github.viacheslavbondarchuk.TaskStatus.CANCELLED;
import static io.github.viacheslavbondarchuk.TaskStatus.ERROR;
import static io.github.viacheslavbondarchuk.TaskStatus.SUCCESS;

@SuppressWarnings({"rawtypes", "unchecked"})
final class Worker extends Thread {
    private final AtomicBoolean alive = new AtomicBoolean(true);
    private final AtomicBoolean parked = new AtomicBoolean(false);
    private final Queue<WorkerTask<?>> queue = new ConcurrentLinkedQueue<>();
    private final LongAdder tasksCount = new LongAdder();

    private final MetricService metrics;

    Worker(String name, ThreadGroup group, MetricService metrics) {
        super(group, name);
        this.metrics = metrics;
        this.start();
        this.init();
    }

    private void init() {
        metrics.gauge(WORKER_TASK_QUEUE_SIZE, getName(), tasksCount, LongAdder::doubleValue);
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
        tasksCount.increment();
        unpark();
    }

    private void _execute(WorkerTask task) {
        metrics.startTimer(WORKER_TASK_EXECUTION_TIME, getName(), task.getKey());
        CompletableFuture future = task.getCompletableFuture();
        if (!future.isCancelled()) {
            try {
                future.complete(task.execute());
                metrics.incrementTaskCount(WORKER_FINISHED_TASKS_COUNT, getName(), task.getKey(), SUCCESS);
            } catch (InterruptedException ie) {
                WorkerUtil.handleInterruptException(ie);
                future.completeExceptionally(ie);
                metrics.incrementTaskCount(WORKER_FINISHED_TASKS_COUNT, getName(), task.getKey(), ERROR);
            } catch (Exception ex) {
                future.completeExceptionally(ex);
                metrics.incrementTaskCount(WORKER_FINISHED_TASKS_COUNT, getName(), task.getKey(), ERROR);
            }
        } else {
            metrics.incrementTaskCount(WORKER_FINISHED_TASKS_COUNT, getName(), task.getKey(), CANCELLED);
        }
        metrics.stopTimer(task.getKey());
    }

    @Override
    public void run() {
        while (alive.get()) {
            WorkerTask<?> task = queue.poll();
            if (task != null) {
                tasksCount.decrement();
                _execute(task);
                continue;
            }
            park();
        }
    }
}
