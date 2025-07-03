package io.github.ryntric;

import com.google.common.hash.HashFunction;

import java.util.concurrent.atomic.AtomicLong;

/**
 * author: vbondarchuk
 * date: 7/3/25
 * time: 7:15 PM
 **/

final class MPRRNodeSelector extends WorkerNodeSelector {
    private final WorkerNode[] nodes;
    private final int length;
    private final AtomicLong next;

    MPRRNodeSelector(HashFunction hashFunction, WorkerNode[] nodes) {
        super(hashFunction);
        this.nodes = nodes;
        this.length = nodes.length;
        this.next = new AtomicLong();
    }

    @Override
    public WorkerNode getNode(long ignored) {
        int index = (int) (next.getAndIncrement() % length);
        return nodes[index];
    }

    @Override
    WorkerNode select(String key) {
        return getNode(0L);
    }

    @Override
    WorkerNode select(int key) {
        return getNode(0L);
    }

    @Override
    WorkerNode select(long key) {
        return getNode(0L);
    }

    @Override
    WorkerNode select(byte[] key) {
        return getNode(0L);
    }
}
