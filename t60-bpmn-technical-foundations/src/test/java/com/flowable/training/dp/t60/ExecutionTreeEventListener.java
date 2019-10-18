package com.flowable.training.dp.t60;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEvent;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.delegate.event.FlowableActivityEvent;
import org.flowable.engine.delegate.event.FlowableSequenceFlowTakenEvent;
import org.flowable.engine.runtime.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ExecutionTreeEventListener implements ActivityEventListener {

    private Map<String, Map<String, Character>> simplifiedExecutionMap = new HashMap<>();

    private List<ExecutionTreeEvent> events = new ArrayList<>();

    public static final class ExecutionTreeEvent {

        private String event;
        private String flowElementId;
        private String execution;
        private String currentExecutionTree;

        public ExecutionTreeEvent(String event, String flowElementId, String execution, String currentExecutionTree) {
            this.event = event;
            this.flowElementId = flowElementId;
            this.execution = execution;
            this.currentExecutionTree = currentExecutionTree;
        }

        public String getExecution() {
            return execution;
        }

        public String getEvent() {
            return event;
        }

        public String getFlowElementId() {
            return flowElementId;
        }

        public String getCurrentExecutionTree() {
            return currentExecutionTree;
        }
    }

    @Autowired
    @Lazy
    private ProcessEngine processEngine;

    @Override
    public void observe(FlowableActivityEvent event) {
        if (!event.getType().name().equals("ACTIVITY_COMPLETED") && !event.getType().name().equals("ACTIVITY_STARTED")) {
            return;
        }

        // init root (A=Root Process Instance)
        simplifiedExecutionId(event.getProcessInstanceId(), event.getProcessInstanceId());

        String simplifiedExecutionId = simplifiedExecutionId(event);

        ExecutionTreeEvent executionTreeEvent = new ExecutionTreeEvent(event.getType().name(), event.getActivityId(), simplifiedExecutionId,
            printCurrentExecutionTree(event.getProcessInstanceId()));
        events.add(executionTreeEvent);
    }

    public List<ExecutionTreeEvent> getEvents() {
        return events;
    }

    @Override
    public void observeSequenceFlowTaken(FlowableSequenceFlowTakenEvent event) {
        ExecutionTreeEvent executionTreeEvent = new ExecutionTreeEvent(event.getType().name(), event.getId(),
            simplifiedExecutionId(event.getProcessInstanceId(), event.getExecutionId()),
            printCurrentExecutionTree(event.getProcessInstanceId()));
        events.add(executionTreeEvent);
    }


    private String printCurrentExecutionTree(String processInstanceId) {
        return printTree(processEngine.getRuntimeService().createExecutionQuery().processInstanceId(processInstanceId).list());
    }

    private String printTree(List<Execution> list) {
        if (list.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        Map<String, List<Execution>> executionsByParent = list.stream().filter(e -> e.getParentId() != null)
            .collect(Collectors.groupingBy(Execution::getParentId));
        Execution pointer = list.stream().filter(e -> e.getParentId() == null).findFirst().get();

        result.append(printSelfAndChildren(pointer, executionsByParent, 1));

        return result.toString();
    }

    private String printSelfAndChildren(Execution pointer, Map<String, List<Execution>> executionsByParent, int depth) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(simplifiedExecutionId(pointer.getProcessInstanceId(), pointer.getId()));
        List<Execution> children = executionsByParent.get(pointer.getId());
        if (children != null) {
            children.forEach(c -> stringBuilder.append("\n").append(StringUtils.repeat("  ", depth)).append(printSelfAndChildren(c, executionsByParent, depth + 1)));
        }
        return stringBuilder.toString();
    }

    private String simplifiedExecutionId(FlowableEngineEvent flowableEvent) {
        return simplifiedExecutionId(flowableEvent.getProcessInstanceId(), flowableEvent.getExecutionId());
    }

    private String simplifiedExecutionId(String processInstanceId, String executionId) {
        Map<String, Character> mapByProcess = this.simplifiedExecutionMap.computeIfAbsent(processInstanceId, p -> new HashMap<>());

        Character character = mapByProcess.get(executionId);
        if (character == null) {
            character = (char) ('A' + mapByProcess.size());
        }
        mapByProcess.put(executionId, character);
        return character.toString();
    }
}

