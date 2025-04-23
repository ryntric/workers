package io.github.viacheslavbondarchuk;

import java.util.concurrent.CompletableFuture;

final class VoidTask extends AbstractWorkerTask<Void> {
    private final Runnable runnable;

    VoidTask(String key, CompletableFuture<Void> future, Runnable runnable) {
        super(key, future);
        this.runnable = runnable;
    }

    @Override
    public Void execute() throws Exception {
        runnable.run();
        return null;
    }
}
