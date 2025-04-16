package io.github.viacheslavbondarchuk;

import java.util.Map;

public interface OnWorkerTaskCompletion {

    void onSuccess(String key, String workerName, Map<String, Object> attributes);

    void onCancel(String key, String workerName, Map<String, Object> attributes);

    void onError(String key, String workerName, Exception ex, Map<String, Object> attributes);
}
