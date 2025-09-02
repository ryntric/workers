package io.github.ryntric;

@Deprecated
interface WorkerTask<R> {
    R execute();

    Long getCreatedAt();

    String getName();
}
