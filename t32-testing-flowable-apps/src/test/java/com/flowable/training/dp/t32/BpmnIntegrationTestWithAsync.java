package com.flowable.training.dp.t32;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.BDDMockito.given;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.job.api.Job;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = { T32WorkApplication.class },
    properties = "flowable.async-executor-activate=true")
@TestPropertySource("/application-test.properties")
public class BpmnIntegrationTestWithAsync {

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
        waitForJobs();
        assertThat(asyncService.getLastGreetingReceived()).isEqualTo("Hello Test");

        // User task
        Task task = processEngine.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(task).isNotNull();
        processEngine.getTaskService().complete(task.getId());

        // Timer
        Job timerJob = processEngine.getManagementService().createTimerJobQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(timerJob).isNotNull();

        processEngine.getProcessEngineConfiguration().getClock().setCurrentTime(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)));

        waitForJobs();

        // Process instance is finished, therefore not present in the runtime any more
        assertThat(processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult())
            .isNull();
    }

    private void waitForJobs() {
        await("jobs-and-timers")
            .atMost(30, TimeUnit.SECONDS)
            .pollDelay(250, TimeUnit.MILLISECONDS)
            .until(() -> processEngine.getManagementService().createJobQuery().count() == 0
                && processEngine.getManagementService().createTimerJobQuery().count() == 0);
    }

    public interface SyncService {

        String call();

    }

}
