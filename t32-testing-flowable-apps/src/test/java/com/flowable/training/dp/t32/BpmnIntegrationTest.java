package com.flowable.training.dp.t32;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.job.api.Job;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = { T32WorkApplication.class })
@ActiveProfiles("test")
public class BpmnIntegrationTest {

    /**
     * Any Engine and any service can be injected here
     */
    @Autowired
    private ProcessEngine processEngine;

    /**
     * Mocked Service
     */
    @MockBean(name = "syncService")
    private SyncService syncService;

    /**
     * Injected service
     */
    @Autowired
    private AsyncService asyncService;

    @Test
    public void bpmnProcess() {
        // Synchronous service call with mocked service bean
        given(this.syncService.call()).willReturn("Hello Test");
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

    public interface SyncService {

        String call();

    }

}
