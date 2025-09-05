package io.github.ryntric;

public final class WorkerServiceTerminatedException extends RuntimeException {
    public WorkerServiceTerminatedException(String name) {
        super(String.format("Worker service is shutdown: %s", name));
    }
}
