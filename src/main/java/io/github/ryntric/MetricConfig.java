package io.github.ryntric;

import io.micrometer.core.instrument.MeterRegistry;

import static io.github.ryntric.MetricTagName.WORKER_NAME;
import static io.github.ryntric.MetricTagName.WORKER_SERVICE_NAME;
import static io.github.ryntric.MetricTagName.WORKER_TASK_NAME;

@Deprecated
class MetricConfig {
    private byte features = (byte) (WORKER_NAME.flag() | WORKER_TASK_NAME.flag() | WORKER_SERVICE_NAME.flag());
    private MeterRegistry meterRegistry;

    MeterRegistry getMeterRegistry() {
        return meterRegistry;
    }

    void setMeterRegistry(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    boolean isTagEnabled(MetricTagName name) {
        return (this.features & (name.flag())) != 0;
    }

    void disableTag(MetricTagName name) {
        MetricConfig.this.features &= (byte) ~name.flag();
    }

    void enableTag(MetricTagName name) {
        MetricConfig.this.features |= name.flag();
    }

}
