[![Maven Central](https://img.shields.io/maven-central/v/io.github.ryntric/workers.svg)](https://search.maven.org/artifact/io.github.ryntric/workers)
[![LICENSE](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/ryntric/workers/blob/master/LICENSE)
[![Maven CI](https://github.com/ryntric/workers/actions/workflows/maven-build.yml/badge.svg)](https://github.com/ryntric/workers/actions/workflows/maven-build.yml)
[![pages-build-deployment](https://github.com/ryntric/workers/actions/workflows/pages/pages-build-deployment/badge.svg)](https://github.com/ryntric/workers/actions/workflows/pages/pages-build-deployment)

Workers is a high-performance Java library for distributing events across multiple worker threads.
It is designed around ring buffers, lock-free sequences, and consistent hashing to achieve predictable throughput and low latency.

### ✨ Features

- ⚡ High performance: lock-free queues with cache-line padding to minimize contention.

- 🎯 Consistent hashing: route events to the correct worker based on a key.

- 🧩 Pluggable factories & handlers: customize how events are created and processed.

- 🛡️ Fault isolation: if one worker fails, others continue to process events.

- 🧵 Thread-aware design: workers run on dedicated threads with controlled lifecycle.

### 📦 Core Concepts

- WorkerService – orchestrates a pool of workers, distributes events based on keys.

- WorkerNode – logical partition that maps to a worker (with replicas for load balancing).

- Worker – lightweight event processor that consumes from a ring buffer.

- EventHandler – user-defined callback for handling events.

- EventFactory – supplier for creating reusable event objects.

### 🚀 Example Usage

```java
private static final EventHandler<Event> EVENT_HANDLER = new EventHandler<>() {
    @Override
    public void onEvent(String name, Event event, long sequence) {
        System.out.println("Worker name: " + name + ", sequence: " + sequence);
    }

    @Override
    public void onError(String name, Event event, long sequence, Throwable ex) {

    }

    @Override
    public void onStart(String name) {

    }

    @Override
    public void onShutdown(String name) {

    }
};
private static final EventFactory<Event> EVENT_FACTORY = Event::new;
private static final EventTranslatorOneArg<Event, Integer>  EVENT_TRANSLATOR = Event::setId;
private static final WorkerService<Event> WORKER_SERVICE = new WorkerService<>("test", EVENT_HANDLER, EVENT_FACTORY, DefaultHashCodeProvider.INSTANCE, WorkerServiceConfig.INSTANCE);

void main(String[] args) {
    WORKER_SERVICE.start();
    for (int i = 0; i < 10_000_000; i++) {
        WORKER_SERVICE.publishEvent(i, EVENT_TRANSLATOR, i);
    }
    WORKER_SERVICE.shutdown();
}

public static class Event {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
```

### ⚠️ Exceptions

WorkerServiceTerminatedException – thrown when a worker service is shut down or no longer available.

### 📊 Performance

Optimized for millions of events per second on modern CPUs.

Scales with number of workers (power of 2 recommended for distribution).
