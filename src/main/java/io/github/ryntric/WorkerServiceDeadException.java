package io.github.ryntric;

final class WorkerServiceDeadException extends RuntimeException {
    public WorkerServiceDeadException(String name) {
        super(String.format("Worker service dead: %s", name));
    }
}
