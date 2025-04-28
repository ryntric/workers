package io.github.viacheslavbondarchuk;

interface MetricService {

    void incrementTaskCount(MetricName name, CompletionTaskStatus status, MetricContext context);

    void startTimer(MetricName name, MetricContext context);

    void stopTimer(MetricContext context);

    void recordLatency(MetricName name, long latency, MetricContext context);

}
