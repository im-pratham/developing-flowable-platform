package com.flowable.training.dp.t32;

import static org.assertj.core.api.Assertions.assertThat;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.engine.test.FlowableTest;
import org.flowable.engine.test.mock.Mocks;
import org.flowable.job.api.Job;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;

@FlowableTest
public class BpmnUnitTest {

    @Test
    @Deployment(resources = { "processes/test-process.bpmn20.xml" })
    public void bpmnProcess(ProcessEngine processEngine) {
        Mocks.register("syncService", new SyncService());
        AsyncService asyncService = new AsyncService();
        Mocks.register("asyncService", asyncService);

        // Synchronous service call with mocked service bean
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey("test-process");
        assertThat(processEngine.getRuntimeService().getVariable(processInstance.getProcessInstanceId(), "greeting"))
            .isEqualTo("Hello Test");

        // Asynchronous service call with 'normal' bean
        Job asyncJob = processEngine.getManagementService().createJobQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(asyncJob).isNotNull();
        processEngine.getManagementService().executeJob(asyncJob.getId());
        assertThat(asyncService.getLastGreetingReceived()).isEqualTo("Hello Test");

        // User task
        Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(task).isNotNull();
        processEngine.getTaskService().complete(task.getId());

        // Timer
        Job timerJob = processEngine.getManagementService().createTimerJobQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(timerJob).isNotNull();
        timerJob = processEngine.getManagementService().moveTimerToExecutableJob(timerJob.getId());
        processEngine.getManagementService().executeJob(timerJob.getId());

        // Process instance is ended, therefore not present in the runtime any more
        assertThat(processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult())
            .isNull();
    }

    public class SyncService {

        public String call() {
            return "Hello Test";
        }

    }

}
