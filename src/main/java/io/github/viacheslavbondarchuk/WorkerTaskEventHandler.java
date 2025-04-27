package io.github.viacheslavbondarchuk;

import com.lmax.disruptor.EventHandler;

import static io.github.viacheslavbondarchuk.MetricService.WORKER_EXECUTION_TIME_LATENCY;
import static io.github.viacheslavbondarchuk.MetricService.WORKER_FINISHED_TASKS_COUNT;
import static io.github.viacheslavbondarchuk.MetricService.WORKER_TASK_EXECUTION_TIME;
import static io.github.viacheslavbondarchuk.CompletionTaskStatus.CANCELLED;
import static io.github.viacheslavbondarchuk.CompletionTaskStatus.ERROR;
import static io.github.viacheslavbondarchuk.CompletionTaskStatus.SUCCESS;

@SuppressWarnings({"unchecked", "rawtypes"})
final class WorkerTaskEventHandler implements EventHandler<WorkerTaskEvent> {
    private final MetricService metrics;

    WorkerTaskEventHandler(MetricService metrics) {
        this.metrics = metrics;
    }

    private String getName() {
        Thread thread = Thread.currentThread();
        return thread.getName();
    }

    @Override
    public void onEvent(WorkerTaskEvent event, long sequence, boolean endOfBatch) {
        WorkerTask task = event.getWorkerTask();
        MetricContext context = new MetricContext(getName(), task.getKey(), task.getName());
        metrics.recordLatency(WORKER_EXECUTION_TIME_LATENCY, task.getCreatedAtNs(), context);
        metrics.startTimer(WORKER_TASK_EXECUTION_TIME, context);
        if (!task.isCancelled()) {
            try {
                task.complete(task.execute());
                metrics.incrementTaskCount(WORKER_FINISHED_TASKS_COUNT, SUCCESS, context);
            } catch (InterruptedException ie) {
                WorkerUtil.handleInterruptException(ie);
                task.completeExceptionally(ie);
                metrics.incrementTaskCount(WORKER_FINISHED_TASKS_COUNT, ERROR, context);
            } catch (Exception ex) {
                task.completeExceptionally(ex);
                metrics.incrementTaskCount(WORKER_FINISHED_TASKS_COUNT, ERROR, context);
            }
        } else {
            metrics.incrementTaskCount(WORKER_FINISHED_TASKS_COUNT, CANCELLED, context);
        }
        metrics.stopTimer(context);
    }
}
