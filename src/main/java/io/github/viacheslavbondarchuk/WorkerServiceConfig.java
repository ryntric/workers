package io.github.viacheslavbondarchuk;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import io.micrometer.core.instrument.MeterRegistry;

public final class WorkerServiceConfig {
    private String name;
    private int workerCount = WorkerUtil.getAvailableProcessors();
    private int replicaCount = 200;
    private HashFunction hashFunction = Hashing.murmur3_32_fixed();
    private MeterRegistry meterRegistry = null;
    private WaitStrategy waitStrategy = new BlockingWaitStrategy();
    private int bufferSize = 4096;

    private WorkerServiceConfig() {}

    public String getName() {
        return name;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public int getReplicaCount() {
        return replicaCount;
    }

    public HashFunction getHashFunction() {
        return hashFunction;
    }

    public MeterRegistry getMeterRegistry() {
        return meterRegistry;
    }

    public WaitStrategy getWaitStrategy() {
        return waitStrategy;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public static Builder builder() {
        return new WorkerServiceConfig().new Builder();
    }

    public class Builder {

        /**
         * Sets name of the WorkerService
         * @param name name of the WorkerService
         * @return this
         */
        public Builder name(String name) {
            WorkerServiceConfig.this.name = name;
            return this;
        }


        /**
         * Sets worker count
         * @param workerCount count of workers
         * @return this
         */
        public Builder workerCount(int workerCount) {
            WorkerServiceConfig.this.workerCount = workerCount;
            return this;
        }

        /**
         * Sets replicaCount that will be used for each worker
         * @param replicaCount replica count
         * @return this
         */
        public Builder replicaCount(int replicaCount) {
            WorkerServiceConfig.this.replicaCount = replicaCount;
            return this;
        }

        /**
         * Sets the hash function that is used for calculating the hash of a task key
         * @param hashFunction - hash function. To read more, follow to {@link com.google.common.hash.HashFunction}
         * @return this
         */
        public Builder hashFunction(HashFunction hashFunction) {
            WorkerServiceConfig.this.hashFunction = hashFunction;
            return this;
        }

        /**
         * Sets meter registry of micrometer
         * @param meterRegistry - meter register
         * @return this
         */
        public Builder meterRegistry(MeterRegistry meterRegistry) {
            WorkerServiceConfig.this.meterRegistry = meterRegistry;
            return this;
        }

        /**
         * Sets a wait strategy that is used for waiting for some event when the task buffer is either full or empty
         * @param waitStrategy wait strategy. To read more follow to {@link com.lmax.disruptor.WaitStrategy}
         * @return this
         */
        public Builder waitStrategy(WaitStrategy waitStrategy) {
            WorkerServiceConfig.this.waitStrategy = waitStrategy;
            return this;
        }

        /**
         * Sets a task buffer size for each worker. Note that the buffer size should be a power of 2, e.g., 512, 1024, 2048, 4096, etc.
         * @param bufferSize buffer size
         * @return this
         */
        public Builder bufferSize(int bufferSize) {
            WorkerServiceConfig.this.bufferSize = bufferSize;
            return this;
        }

        public WorkerServiceConfig build() {
            return WorkerServiceConfig.this;
        }
    }


}
