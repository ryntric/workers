package io.github.ryntric;

import com.google.common.hash.HashFunction;

/**
 * author: vbondarchuk
 * date: 7/3/25
 * time: 7:11 PM
 **/

@Deprecated
final class SPRRNodeSelector extends WorkerNodeSelector {
    private final WorkerNode[] nodes;
    private final int length;

    private long next;

    SPRRNodeSelector(HashFunction hashFunction, WorkerNode[] nodes) {
        super(hashFunction);
        this.nodes = nodes;
        this.length = nodes.length;
    }

    @Override
    public WorkerNode getNode(long ignored) {
        int index = (int) (next++ % length);
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
