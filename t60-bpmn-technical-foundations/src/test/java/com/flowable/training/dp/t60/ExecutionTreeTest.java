package com.flowable.training.dp.t60;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = { T60WorkApplication.class })
@TestPropertySource("/application-test.properties")
public class ExecutionTreeTest {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private ExecutionTreeEventListener listener;

    @Test
    public void debugExecutionTree() throws IOException {
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey("execution-tree-v6");

        List<Task> tasks = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).list();
        tasks.forEach(task -> processEngine.getTaskService().complete(task.getId()));

        tasks = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).list();
        tasks.forEach(task -> processEngine.getTaskService().complete(task.getId()));

        tasks = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).list();
        tasks.forEach(task -> processEngine.getTaskService().complete(task.getId()));

        List<ExecutionTreeEventListener.ExecutionTreeEvent> events = listener.getEvents();

        // print to csv
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(events.stream().map(this::printLine).collect(Collectors.joining("\n")));
        assertEquals(IOUtils.toString(ExecutionTreeTest.class.getResourceAsStream("/execution-log.csv"), StandardCharsets.UTF_8),
            stringBuilder.toString());

        listener.getEvents().clear();
    }

    private String printLine(ExecutionTreeEventListener.ExecutionTreeEvent event) {
        return String.format("%s,%s,%s,\"%s\"", event.getEvent(), event.getFlowElementId(), event.getExecution(), event.getCurrentExecutionTree());
    }

}
