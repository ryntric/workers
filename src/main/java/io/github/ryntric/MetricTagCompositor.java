package io.github.ryntric;

import io.micrometer.core.instrument.Tags;

@Deprecated
final class MetricTagCompositor {
    private final MetricConfig metricConfig;
    private Tags tags = Tags.empty();

    MetricTagCompositor(MetricConfig metricConfig) {
        this.metricConfig = metricConfig;
    }

    public MetricTagCompositor add(MetricTagName tagName, String value) {
        if (value != null && metricConfig.isTagEnabled(tagName)) {
            this.tags = tags.and(tagName.value(), value);
        }
        return this;
    }

    public Tags tags() {
        return tags;
    }

    public void clear() {
        tags = Tags.empty();
    }
}
