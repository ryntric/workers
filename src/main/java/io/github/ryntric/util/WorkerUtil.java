package io.github.ryntric.util;

public final class WorkerUtil {
    private static final Runtime RUNTIME = Runtime.getRuntime();
    private static final Integer AVAILABLE_PROCESSORS = RUNTIME.availableProcessors();

    private WorkerUtil() {}

    public static int getWorkerCount() {
        return AVAILABLE_PROCESSORS > 1 ? AVAILABLE_PROCESSORS - 1 : AVAILABLE_PROCESSORS;
    }

    public static int getAvailableProcessors() {
        return AVAILABLE_PROCESSORS;
    }

    public static int ceilPowerOfTwo(int n) {
        if (n <= 0) return 1;
        return Integer.highestOneBit(n - 1) << 1;
    }

}
