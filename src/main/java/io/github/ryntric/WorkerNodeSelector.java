package io.github.ryntric;

import com.google.common.hash.HashFunction;

/**
 * author: vbondarchuk
 * date: 7/3/25
 * time: 6:44 PM
 **/

abstract class WorkerNodeSelector {
    protected final HashFunction hashFunction;

    public WorkerNodeSelector(HashFunction hashFunction) {
        this.hashFunction = hashFunction;
    }

    abstract WorkerNode getNode(long keyHashCode);

    WorkerNode select(String key) {
        return getNode(WorkerUtil.getKeyHash(key, hashFunction));
    }

    WorkerNode select(int key) {
        return getNode(WorkerUtil.getKeyHash(key, hashFunction));
    }

    WorkerNode select(long key) {
        return getNode(WorkerUtil.getKeyHash(key, hashFunction));
    }

    WorkerNode select(byte[] key) {
        return getNode(WorkerUtil.getKeyHash(key, hashFunction));
    }

}
