package io.github.ryntric;

import io.github.ryntric.EventTranslator.EventTranslatorFiveArg;

public final class WorkerNode<T> {
    private final Worker<T> worker;

    public WorkerNode(Worker<T> worker) {
        this.worker = worker;
    }

    public <A> void publishEvent(EventTranslator.EventTranslatorOneArg<T, A> translator, A arg) {
        worker.publishEvent(translator, arg);
    }

    public <A> void publishEvents(EventTranslator.EventTranslatorOneArg<T, A> translator, A[] args) {
        worker.publishEvents(translator, args);
    }

    public <A, B> void publishEvent(EventTranslator.EventTranslatorTwoArg<T, A, B> translator, A arg0, B arg1) {
        worker.publishEvent(translator, arg0, arg1);
    }

    public <A, B> void publishEvents(EventTranslator.EventTranslatorTwoArg<T, A, B> translator, A[] arg0, B[] arg1) {
        worker.publishEvents(translator, arg0, arg1);
    }

    public <A, B, C> void publishEvent(EventTranslator.EventTranslatorThreeArg<T, A, B, C> translator, A arg0, B arg1, C arg2) {
        worker.publishEvent(translator, arg0, arg1, arg2);
    }

    public <A, B, C> void publishEvents(EventTranslator.EventTranslatorThreeArg<T, A, B, C> translator, A[] arg0, B[] arg1, C[] arg2) {
        worker.publishEvents(translator, arg0, arg1, arg2);
    }

    public <A, B, C, D> void publishEvent(EventTranslator.EventTranslatorFourArg<T, A, B, C, D> translator, A arg0, B arg1, C arg2, D arg3) {
        worker.publishEvent(translator, arg0, arg1, arg2, arg3);
    }

    public <A, B, C, D> void publishEvents(EventTranslator.EventTranslatorFourArg<T, A, B, C, D> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3) {
        worker.publishEvents(translator, arg0, arg1, arg2, arg3);
    }

    public <A, B, C, D, E> void publishEvent(EventTranslatorFiveArg<T, A, B, C, D, E> translator, A arg0, B arg1, C arg2, D arg3, E arg4) {
        worker.publishEvent(translator, arg0, arg1, arg2, arg3, arg4);
    }

    public <A, B, C, D, E> void publishEvents(EventTranslatorFiveArg<T, A, B, C, D, E> translator, A[] arg0, B[] arg1, C[] arg2, D[] arg3, E[] arg4) {
        worker.publishEvents(translator, arg0, arg1, arg2, arg3, arg4);
    }
}
