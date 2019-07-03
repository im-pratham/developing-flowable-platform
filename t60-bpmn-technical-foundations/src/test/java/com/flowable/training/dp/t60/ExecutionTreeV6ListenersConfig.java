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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.flowable.spring.boot.EngineConfigurationConfigurer;

@Configuration
public class ExecutionTreeV6ListenersConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    @Autowired
    private ActivitiyEventListener activitiyEventListener;

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
        eventListeners.addAll(flowableEventListeners(activitiyEventListener));
    }

    private Collection<FlowableEventListener> flowableEventListeners(ActivitiyEventListener activitiyEventListener) {
        return Collections.singleton(new EventLogger(activitiyEventListener));
    }

    private static final class EventLogger implements FlowableEventListener {

        private static final Logger LOGGER = LoggerFactory.getLogger(EventLogger.class);

        private final ActivitiyEventListener activitiyEventListener;

        private EventLogger(ActivitiyEventListener activitiyEventListener) {
            this.activitiyEventListener = activitiyEventListener;
        }

        @Override
        public void onEvent(FlowableEvent event) {
            if (event instanceof FlowableActivityEvent) {
                activitiyEventListener.observe((FlowableActivityEvent) event);
            } else if (event instanceof FlowableSequenceFlowTakenEvent) {
                activitiyEventListener.observeSequenceFlowTaken((FlowableSequenceFlowTakenEvent) event);
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
