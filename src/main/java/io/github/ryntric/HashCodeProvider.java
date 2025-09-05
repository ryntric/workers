package io.github.ryntric;


/**
 * Strategy interface for computing hash codes from different types of keys.
 * Typical implementations may use simple wrappers around
 * {@link Object#hashCode()} or custom hashing algorithms optimized for specific use cases.
 */
public interface HashCodeProvider {

    int provide(String key);

    int provide(int key);

    int provide(long key);

    int provide(byte[] key);
}
