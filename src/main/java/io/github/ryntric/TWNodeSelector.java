package io.github.ryntric;

import com.google.common.hash.HashFunction;

import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * author: vbondarchuk
 * date: 7/3/25
 * time: 7:03 PM
 **/

final class TWNodeSelector extends WorkerNodeSelector {
    private final NavigableMap<Long, WorkerNode> ring = new TreeMap<>();

    TWNodeSelector(HashFunction hashFunction, WorkerNode[] nodes) {
        super(hashFunction);
        for (WorkerNode node : nodes) {
            ring.put(WorkerUtil.getKeyHash(node.getName(), hashFunction), node);
        }
    }

    @Override
    public WorkerNode getNode(long keyHashCode) {
        SortedMap<Long, WorkerNode> tailed = ring.tailMap(keyHashCode);
        long workerNodeId = tailed.isEmpty() ? ring.firstKey() : tailed.firstKey();
        return ring.get(workerNodeId);
    }
}
