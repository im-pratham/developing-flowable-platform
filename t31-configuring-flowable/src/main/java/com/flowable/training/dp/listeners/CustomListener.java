package com.flowable.training.dp.listeners;


import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomListener implements FlowableEventListener {

    private final static Logger log = LoggerFactory.getLogger(CustomListener.class);

    @Override
    public void onEvent(FlowableEvent event) {
        log.info("Event type: {} received.", event.getType().name());
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    @Override
    public String getOnTransaction() {
        return null;
    }


    @Override
    public String toString() {
        return "Custom Listener to log the name of the type of events received by the engine.";
    }
}
