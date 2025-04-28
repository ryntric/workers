package io.github.viacheslavbondarchuk;

interface MetricService {

    void incrementTaskCount(MetricName metricName, CompletionTaskStatus status, MetricContext context);

    void startTimer(MetricName metricName, MetricContext context);

    void stopTimer(MetricContext context);

    void recordLatency(MetricName metricName, long latency, MetricContext context);

}
