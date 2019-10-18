package com.flowable.training.dp.t60;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.impl.cfg.TransactionState;
import org.flowable.engine.delegate.event.FlowableActivityEvent;
import org.flowable.engine.delegate.event.FlowableSequenceFlowTakenEvent;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.flowable.spring.boot.EngineConfigurationConfigurer;

@Configuration
public class ExecutionTreeV6ListenersConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    @Autowired
    private ActivityEventListener activityEventListener;

    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        /*
         * Registering untyped ("catch all") event listeners
         */
        List<FlowableEventListener> eventListeners = engineConfiguration.getEventListeners();
        if (eventListeners == null) {
            eventListeners = new ArrayList<>();
        }
        engineConfiguration.setEventListeners(eventListeners);
        eventListeners.addAll(flowableEventListeners(activityEventListener));
    }

    private Collection<FlowableEventListener> flowableEventListeners(ActivityEventListener activityEventListener) {
        return Collections.singleton(new EventLogger(activityEventListener));
    }

    private static final class EventLogger implements FlowableEventListener {

        private final ActivityEventListener activityEventListener;

        private EventLogger(ActivityEventListener activityEventListener) {
            this.activityEventListener = activityEventListener;
        }

        @Override
        public void onEvent(FlowableEvent event) {
            if (event instanceof FlowableActivityEvent) {
                activityEventListener.observe((FlowableActivityEvent) event);
            } else if (event instanceof FlowableSequenceFlowTakenEvent) {
                activityEventListener.observeSequenceFlowTaken((FlowableSequenceFlowTakenEvent) event);
            }
        }

        @Override
        public boolean isFailOnException() {
            return false;
        }

        @Override
        public boolean isFireOnTransactionLifecycleEvent() {
            return true;
        }

        @Override
        public String getOnTransaction() {
            return TransactionState.COMMITTED.name();
        }

    }

}
