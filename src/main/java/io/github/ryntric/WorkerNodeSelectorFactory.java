package io.github.ryntric;

import com.google.common.hash.HashFunction;

/**
 * author: vbondarchuk
 * date: 7/3/25
 * time: 7:20 PM
 **/

@Deprecated
final class WorkerNodeSelectorFactory {
    private WorkerNodeSelectorFactory() {}

    static WorkerNodeSelector create(WorkerNode[] nodes, WorkerNodeSelectorType type, HashFunction hashFunction) {
        WorkerNodeSelector selector = null;
        switch (type) {
            case TAIL_WRAP:
                selector = new TWNodeSelector(hashFunction, nodes);
                break;
            case MODULO:
                selector = new ModuloNodeSelector(hashFunction, nodes);
                break;
            case ROUND_ROBIN_SINGLE_PRODUCER:
                selector = new SPRRNodeSelector(hashFunction, nodes);
                break;
            case ROUND_ROBIN_MULTI_PRODUCER:
                selector = new MPRRNodeSelector(hashFunction, nodes);
                break;
        }
        return selector;
    }
}
