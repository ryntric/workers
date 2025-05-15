package io.github.ryntric;

import io.micrometer.core.instrument.Tags;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.github.ryntric.MetricTagName.WORKER_NAME;
import static io.github.ryntric.MetricTagName.WORKER_SERVICE_NAME;
import static io.github.ryntric.MetricTagName.WORKER_TASK_COMPLETION_STATUS;
import static io.github.ryntric.MetricTagName.WORKER_TASK_NAME;

class MetricTagCompositorTest {

    @Test
    public void testAllEnabled() {
        MetricConfig config = new MetricConfig();
        for (MetricTagName tagName : MetricTagName.values()) {
            config.enableTag(tagName);
        }

        Tags tags = new MetricTagCompositor(config).add(WORKER_NAME, WORKER_NAME.name())
                .add(WORKER_SERVICE_NAME, WORKER_SERVICE_NAME.name())
                .add(WORKER_TASK_NAME, WORKER_TASK_NAME.name())
                .add(WORKER_TASK_COMPLETION_STATUS, WORKER_TASK_COMPLETION_STATUS.name())
                .tags();

        Assertions.assertEquals(4, tags.stream().count());
    }

    @Test
    public void testWhenOnly1Enabled() {
        MetricConfig config = new MetricConfig();
        for (MetricTagName tagName : MetricTagName.values()) {
            config.disableTag(tagName);
        }
        config.enableTag(WORKER_SERVICE_NAME);

        Tags tags = new MetricTagCompositor(config).add(WORKER_NAME, WORKER_NAME.name())
                .add(WORKER_SERVICE_NAME, WORKER_SERVICE_NAME.name())
                .add(WORKER_TASK_NAME, WORKER_TASK_NAME.name())
                .add(WORKER_TASK_COMPLETION_STATUS, WORKER_TASK_COMPLETION_STATUS.name())
                .tags();

        Assertions.assertEquals(1, tags.stream().count());
        Assertions.assertEquals(WORKER_SERVICE_NAME.value(), tags.iterator().next().getKey());
    }

    @Test
    public void testAllDisabled() {
        MetricConfig config = new MetricConfig();
        for (MetricTagName tagName : MetricTagName.values()) {
            config.disableTag(tagName);
        }

        Tags tags = new MetricTagCompositor(config)
                .add(WORKER_NAME, WORKER_NAME.name())
                .add(WORKER_SERVICE_NAME, WORKER_SERVICE_NAME.name())
                .add(WORKER_TASK_NAME, WORKER_TASK_NAME.name())
                .add(WORKER_TASK_COMPLETION_STATUS, WORKER_TASK_COMPLETION_STATUS.name())
                .tags();

        Assertions.assertEquals(0, tags.stream().count());
    }

    @Test
    public void testWhenOnly1Disabled() {
        MetricConfig config = new MetricConfig();
        for (MetricTagName tagName : MetricTagName.values()) {
            config.enableTag(tagName);
        }
        config.disableTag(WORKER_SERVICE_NAME);

        Tags tags = new MetricTagCompositor(config).add(WORKER_NAME, WORKER_NAME.name())
                .add(WORKER_SERVICE_NAME, WORKER_SERVICE_NAME.name())
                .add(WORKER_TASK_NAME, WORKER_TASK_NAME.name())
                .add(WORKER_TASK_COMPLETION_STATUS, WORKER_TASK_COMPLETION_STATUS.name())
                .tags();

        Assertions.assertEquals(3, tags.stream().count());
        Assertions.assertTrue(tags.stream()
                .noneMatch(tag -> tag.getKey().equals(WORKER_SERVICE_NAME.value())));
    }

}
