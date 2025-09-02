package io.github.ryntric;

@Deprecated
interface MetricService {

    MetricContext newMetricContext(String workerName, String taskName);

    void incrementTaskCount(MetricName metricName, MetricContext context);

    MetricTimerContext startTimer(MetricName metricName, MetricContext context);

    void stopTimer(MetricTimerContext context);

    void recordLatency(MetricName metricName, long latency, MetricContext context);

}
