package io.github.ryntric;

/**
 * author: vbondarchuk
 * date: 7/3/25
 * time: 7:00 PM
 **/

@Deprecated
public enum WorkerNodeSelectorType {
    TAIL_WRAP,
    MODULO,
    ROUND_ROBIN_SINGLE_PRODUCER,
    ROUND_ROBIN_MULTI_PRODUCER,
}
