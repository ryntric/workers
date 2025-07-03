package io.github.ryntric;

import com.lmax.disruptor.EventHandler;

import java.util.concurrent.CompletableFuture;

import static io.github.ryntric.MetricName.WORKER_EXECUTION_TIME_LATENCY_MS;
import static io.github.ryntric.MetricName.WORKER_FINISHED_TASKS_COUNT;
import static io.github.ryntric.MetricName.WORKER_TASK_EXECUTION_TIME_MS;

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
        MetricContext context = metrics.newMetricContext(getName(), task.getName());
        metrics.recordLatency(WORKER_EXECUTION_TIME_LATENCY_MS, ClockUtil.diffMillis(task.getCreatedAt()), context);
        MetricTimerContext metricTimerContext = metrics.startTimer(WORKER_TASK_EXECUTION_TIME_MS, context);
        if (!event.isCancelled()) {
            try {
                future.complete(task.execute());
                metrics.incrementTaskCount(WORKER_FINISHED_TASKS_COUNT, context);
            } catch (Exception ex) {
                future.completeExceptionally(ex);
                metrics.incrementTaskCount(WORKER_FINISHED_TASKS_COUNT, context);
            }
        } else {
            metrics.incrementTaskCount(WORKER_FINISHED_TASKS_COUNT, context);
        }
        metrics.stopTimer(metricTimerContext);
        event.clear();
    }
}
