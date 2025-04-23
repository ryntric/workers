package io.github.viacheslavbondarchuk;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class WorkerUtil {
    private static final Runtime RUNTIME = Runtime.getRuntime();
    private static final Integer AVAILABLE_PROCESSORS = RUNTIME.availableProcessors();

    private WorkerUtil() {}

    public static int getAvailableProcessors() {
        return AVAILABLE_PROCESSORS > 1 ? AVAILABLE_PROCESSORS - 1 : AVAILABLE_PROCESSORS;
    }

    /**
     * Returns a hash code of the key
     */
    public static long getKeyHash(String key, HashFunction hashFunction, Charset charset) {
        HashCode hashCode = hashFunction.hashString(key, charset);
        return hashCode.bits() > 32 ? hashCode.asLong() : hashCode.asInt();
    }

    /**
     * Returns a hash code of key
     */
    public static long getKeyHash(String key, HashFunction hashFunction) {
        return WorkerUtil.getKeyHash(key, hashFunction, StandardCharsets.UTF_8);
    }

    /**
     * Handles an interrupt exception
     */
    public static void handleInterruptException(InterruptedException ignored) {
        Thread currentThread = Thread.currentThread();
        currentThread.interrupt();
    }

}
