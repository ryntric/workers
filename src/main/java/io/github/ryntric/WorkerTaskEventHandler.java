package io.github.ryntric;

import com.lmax.disruptor.EventHandler;

import java.util.concurrent.CompletableFuture;

import static io.github.ryntric.CompletionTaskStatus.CANCELLED;
import static io.github.ryntric.CompletionTaskStatus.ERROR;
import static io.github.ryntric.CompletionTaskStatus.SUCCESS;
import static io.github.ryntric.MetricName.WORKER_EXECUTION_TIME_LATENCY;
import static io.github.ryntric.MetricName.WORKER_FINISHED_TASKS_COUNT;
import static io.github.ryntric.MetricName.WORKER_TASK_EXECUTION_TIME;

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
        WorkerTask task = event.getTask();
        CompletableFuture future = event.getFuture();
        MetricContext context = new MetricContext(getName(), task.getName());
        metrics.recordLatency(WORKER_EXECUTION_TIME_LATENCY, ClockUtil.diffMillis(task.getCreatedAt()), context);
        MetricTimerContext metricTimerContext = metrics.startTimer(WORKER_TASK_EXECUTION_TIME, context);
        if (!event.isCancelled()) {
            try {
                future.complete(task.execute());
                metrics.incrementTaskCount(WORKER_FINISHED_TASKS_COUNT, SUCCESS, context);
            } catch (Exception ex) {
                future.completeExceptionally(ex);
                metrics.incrementTaskCount(WORKER_FINISHED_TASKS_COUNT, ERROR, context);
            }
        } else {
            metrics.incrementTaskCount(WORKER_FINISHED_TASKS_COUNT, CANCELLED, context);
        }
        metrics.stopTimer(metricTimerContext);
        event.clear();
    }
}
