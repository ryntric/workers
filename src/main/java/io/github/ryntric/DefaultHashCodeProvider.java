package io.github.ryntric;


import java.util.Arrays;

/**
 * Default implementation of {@link HashCodeProvider}.
 * <p>
 * Uses the built-in Java {@code hashCode()} methods for different key types
 * and applies a simple normalization step
 * ({@code hash ^ (hash >>> 16)}) to improve hash code distribution
 * when used in hash-based data structures.
 * <p>
 * This normalization technique is similar to the one used in
 * {@link java.util.HashMap} to reduce collisions caused by
 * poorly distributed higher-order bits.
 */
public final class DefaultHashCodeProvider implements HashCodeProvider {
    public static final DefaultHashCodeProvider INSTANCE = new DefaultHashCodeProvider();

    private DefaultHashCodeProvider() {}

    private int normalize(int hashcode) {
        return hashcode ^ (hashcode >>> 16);
    }

    @Override
    public int provide(String key) {
        return normalize(key.hashCode());
    }

    @Override
    public int provide(int key) {
        return normalize(key);
    }

    @Override
    public int provide(long key) {
        return normalize(Long.hashCode(key));
    }

    @Override
    public int provide(byte[] key) {
        return normalize(Arrays.hashCode(key));
    }
}
