package io.github.ryntric;

import io.github.ryntric.util.Util;
import io.github.ryntric.util.WorkerUtil;

/**
 * Configuration class for {@code WorkerService}.
 * <p>
 * Provides tunable parameters such as worker count, replica count,
 * buffer size, sequencing strategy, and wait policies.
 * <p>
 * A default singleton instance is available via {@link #INSTANCE}.
 * To create customized configurations, use the {@link Builder}.
 */
public final class WorkerServiceConfig {
    public static WorkerServiceConfig INSTANCE = new WorkerServiceConfig();

    private int workerCount = WorkerUtil.getWorkerCount();
    private int replicaCount = 400;
    private WaitPolicy producerWaitPolicy = WaitPolicy.SPINNING;
    private WaitPolicy consumerWaitPolicy = WaitPolicy.PARKING;
    private int bufferSize = 4096;
    private SequencerType sequencerType = SequencerType.MULTI_PRODUCER;
    private BatchSizeLimit limit = BatchSizeLimit._1_2;

    private WorkerServiceConfig() {}

    /**
     * @return number of worker threads
     */
    public int getWorkerCount() {
        return workerCount;
    }

    /**
     * @return number of replicas used for consistent hashing or partitioning
     */
    public int getReplicaCount() {
        return replicaCount;
    }

    /**
     * @return the wait policy used by producers when publishing events
     */
    public WaitPolicy getProducerWaitPolicy() {
        return producerWaitPolicy;
    }

    /**
     * @return the wait policy used by consumers when waiting for events
     */
    public WaitPolicy getConsumerWaitPolicy() {
        return consumerWaitPolicy;
    }

    /**
     * @return the size of the ring buffer
     */
    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * @return the sequencer type (e.g., single vs. multi-producer)
     */
    public SequencerType getSequencerType() {
        return sequencerType;
    }

    /**
     * @return the maximum batch size limit for event processing
     */
    public BatchSizeLimit getBatchSizeLimit() {
        return limit;
    }

    /**
     * Creates a new builder for customizing {@code WorkerServiceConfig}.
     *
     * @return a new {@link Builder}
     */
    public static Builder builder() {
        return new WorkerServiceConfig().new Builder();
    }

    public class Builder {

        /**
         * Sets the number of worker threads.
         *
         * @param workerCount number of workers
         * @return this builder
         */
        public Builder workerCount(int workerCount) {
            WorkerServiceConfig.this.workerCount = workerCount;
            return this;
        }

        /**
         * Sets the number of replicas.
         *
         * @param replicaCount replica count
         * @return this builder
         */
        public Builder replicaCount(int replicaCount) {
            WorkerServiceConfig.this.replicaCount = replicaCount;
            return this;
        }

        /**
         * Sets the wait policy for producers.
         *
         * @param waitPolicy producer wait policy
         * @return this builder
         */
        public Builder producerWaitPolicy(WaitPolicy waitPolicy) {
            WorkerServiceConfig.this.producerWaitPolicy = waitPolicy;
            return this;
        }

        /**
         * Sets the wait policy for consumers.
         *
         * @param waitPolicy consumer wait policy
         * @return this builder
         */
        public Builder consumerWaitPolicy(WaitPolicy waitPolicy) {
            WorkerServiceConfig.this.consumerWaitPolicy = waitPolicy;
            return this;
        }

        /**
         * Sets the sequencer type for the ring buffer.
         *
         * @param sequencerType sequencer type
         * @return this builder
         */
        public Builder sequencerType(SequencerType sequencerType) {
            WorkerServiceConfig.this.sequencerType = sequencerType;
            return this;
        }

        /**
         * Sets the batch size limit.
         *
         * @param batchSizeLimit batch size limit
         * @return this builder
         */
        public Builder batchSizeLimit(BatchSizeLimit batchSizeLimit) {
            WorkerServiceConfig.this.limit = batchSizeLimit;
            return this;
        }

        /**
         * Sets the ring buffer size. Must be pow of 2
         *
         * @param bufferSize buffer size
         * @return this builder
         */
        public Builder bufferSize(int bufferSize) {
            WorkerServiceConfig.this.bufferSize = Util.assertThatPowerOfTwo(bufferSize);
            return this;
        }

        /**
         * Builds the configured {@link WorkerServiceConfig} instance.
         *
         * @return the configured {@link WorkerServiceConfig}
         */
        public WorkerServiceConfig build() {
            return WorkerServiceConfig.this;
        }
    }


}
