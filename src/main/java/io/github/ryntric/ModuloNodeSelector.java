package io.github.ryntric;

import com.google.common.hash.HashFunction;

/**
 * author: vbondarchuk
 * date: 7/3/25
 * time: 7:08 PM
 **/

@Deprecated
final class ModuloNodeSelector extends WorkerNodeSelector {
    private final WorkerNode[] nodes;
    private final long length;

    ModuloNodeSelector(HashFunction hashFunction, WorkerNode[] nodes) {
        super(hashFunction);
        this.nodes = nodes;
        this.length = nodes.length;
    }

    @Override
    public WorkerNode getNode(long keyHashCode) {
        int index = (int) Math.abs(keyHashCode % length);
        return nodes[index];
    }
}
