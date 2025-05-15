package io.github.ryntric;

interface MetricService {

    void incrementTaskCount(MetricName metricName, CompletionTaskStatus status, MetricContext context);

    MetricTimerContext startTimer(MetricName metricName, MetricContext context);

    void stopTimer(MetricTimerContext context);

    void recordLatency(MetricName metricName, long latency, MetricContext context);

}
