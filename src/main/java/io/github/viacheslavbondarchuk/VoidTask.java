package io.github.viacheslavbondarchuk;

import java.util.concurrent.CompletableFuture;

final class VoidTask extends AbstractWorkerTask<Void> {
    private final Runnable runnable;

    VoidTask(String key, CompletableFuture<Void> future, Runnable runnable) {
        this(key, future, runnable, null);
    }

    VoidTask(String key, CompletableFuture<Void> future, Runnable runnable, String name) {
        super(key, future, name);
        this.runnable = runnable;
    }

    @Override
    public Void execute() throws Exception {
        runnable.run();
        return null;
    }
}
