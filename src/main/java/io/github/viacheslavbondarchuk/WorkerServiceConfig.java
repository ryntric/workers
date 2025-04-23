package io.github.viacheslavbondarchuk;

import com.google.common.hash.HashFunction;
import io.micrometer.core.instrument.MeterRegistry;

public final class WorkerServiceConfig {
    private String name;
    private int workerCount;
    private int replicaCount;
    private HashFunction hashFunction;
    private MeterRegistry meterRegistry;

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

    public static Builder builder() {
        return new WorkerServiceConfig().new Builder();
    }

    public MeterRegistry getMeterRegistry() {
        return meterRegistry;
    }

    public class Builder {
        public Builder name(String name) {
            WorkerServiceConfig.this.name = name;
            return this;
        }

        public Builder workerCount(int workerCount) {
            WorkerServiceConfig.this.workerCount = workerCount;
            return this;
        }

        public Builder replicaCount(int replicaCount) {
            WorkerServiceConfig.this.replicaCount = replicaCount;
            return this;
        }

        public Builder hashFunction(HashFunction hashFunction) {
            WorkerServiceConfig.this.hashFunction = hashFunction;
            return this;
        }

        public Builder meterRegistry(MeterRegistry meterRegistry) {
            WorkerServiceConfig.this.meterRegistry = meterRegistry;
            return this;
        }

        public WorkerServiceConfig build() {
            return WorkerServiceConfig.this;
        }
    }


}
