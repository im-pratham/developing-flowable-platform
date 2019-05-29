package com.flowable.training.dp.t56;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.FlowableListener;
import org.flowable.bpmn.model.ImplementationType;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.impl.cfg.TransactionState;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableActivityEvent;
import org.flowable.engine.impl.bpmn.parser.BpmnParse;
import org.flowable.engine.impl.bpmn.parser.handler.AbstractBpmnParseHandler;
import org.flowable.engine.parse.BpmnParseHandler;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.task.service.delegate.DelegateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import com.flowable.spring.boot.EngineConfigurationConfigurer;

@Configuration
public class BpmnListenersConfiguration implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

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
        eventListeners.addAll(flowableEventListeners());

        /*
         * Registering typed event listeners
         */
        Map<String, List<FlowableEventListener>> typedEventListeners = engineConfiguration.getTypedEventListeners();
        if (typedEventListeners == null) {
            typedEventListeners = new HashMap<>();
        }
        engineConfiguration.setTypedEventListeners(typedEventListeners);
        List<FlowableEventListener> activityStartListeners = typedEventListeners
            .computeIfAbsent(FlowableEngineEventType.ACTIVITY_STARTED.name(), n -> new ArrayList<>());
        activityStartListeners.add(activityStartedListener());

        /*
         * Registering listeners during parse time
         */
        List<BpmnParseHandler> postBpmnParseHandlers = engineConfiguration.getPostBpmnParseHandlers();
        if (postBpmnParseHandlers == null) {
            postBpmnParseHandlers = new ArrayList<>();
        }
        engineConfiguration.setPostBpmnParseHandlers(postBpmnParseHandlers);
        postBpmnParseHandlers.add(new CustomBpmnParseHandler());
    }

    private ActivityStartedListener activityStartedListener() {
        return new ActivityStartedListener();
    }

    private Collection<FlowableEventListener> flowableEventListeners() {
        return Collections.singleton(new EventLogger());
    }

    private static final class ActivityStartedListener extends AbstractFlowableEngineEventListener {

        private static final Logger LOGGER = LoggerFactory.getLogger(ActivityStartedListener.class);

        @Override
        protected void activityStarted(FlowableActivityEvent event) {
            LOGGER.info("Activity '{}' started in process '{}'", event.getActivityId(), event.getProcessInstanceId());
        }

    }

    private static final class EventLogger implements FlowableEventListener {

        private static final Logger LOGGER = LoggerFactory.getLogger(EventLogger.class);

        @Override
        public void onEvent(FlowableEvent event) {
            LOGGER.info("Event occured: {} of class: {}", event.getType().name(), event.getClass().getSimpleName());
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

    private static class CustomBpmnParseHandler extends AbstractBpmnParseHandler<UserTask> {

        @Override
        protected Class<? extends BaseElement> getHandledType() {
            return UserTask.class;
        }

        @Override
        protected void executeParse(BpmnParse bpmnParse, UserTask element) {
            FlowableListener listener = new FlowableListener();
            listener.setImplementationType(ImplementationType.IMPLEMENTATION_TYPE_INSTANCE);
            listener.setInstance(new DocumentationLoggingTaskListener(element.getDocumentation()));
            listener.setEvent(TaskListener.EVENTNAME_CREATE);
            element.getTaskListeners().add(listener);
        }

        private static class DocumentationLoggingTaskListener implements TaskListener {

            private static final Logger LOGGER = LoggerFactory.getLogger(DocumentationLoggingTaskListener.class);

            private static final long serialVersionUID = 8277838624475899346L;

            private final String documentation;

            private DocumentationLoggingTaskListener(String documentation) {
                this.documentation = documentation;
            }

            @Override
            public void notify(DelegateTask delegateTask) {
                LOGGER.info("Creating task with documentation: {}", documentation);
            }

        }

    }
}
