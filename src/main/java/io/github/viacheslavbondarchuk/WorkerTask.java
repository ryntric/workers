package io.github.viacheslavbondarchuk;

interface WorkerTask<R> {
    R execute() throws Exception;

    String getKey();

    Long getCreatedAtNs();

    String getName();

    void complete(R result);

    void completeExceptionally(Throwable ex);

    boolean isCancelled();
}
