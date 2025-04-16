package io.github.viacheslavbondarchuk;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

final class VoidTask extends AbstractWorkerTask<Void> {
    private final Runnable runnable;

    VoidTask(String key, CompletableFuture<Void> future, Runnable runnable, Map<String, Object> attributes) {
        super(key, future, attributes);
        this.runnable = runnable;
    }

    @Override
    Void _execute() throws Exception {
        runnable.run();
        return null;
    }

}
