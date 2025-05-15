package io.github.ryntric;

interface WorkerTask<R> {
    R execute();

    Long getCreatedAt();

    String getName();
}
