package io.github.ryntric;

public abstract class AbstractWorkerTask<R> implements WorkerTask<R> {
    private final Long createdAt = ClockUtil.inMillis();

    @Override
    public final Long getCreatedAt() {
        return createdAt;
    }

    @Override
    public String getName() {
        return null;
    }
}
