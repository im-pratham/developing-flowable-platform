package com.flowable.training.dp.t32;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.job.api.Job;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = { T32WorkApplication.class })
@TestPropertySource("/application-test.properties")
public class IntegrationTest {

    @Autowired
    private ProcessEngine processEngine;

    @MockBean(name = "syncService")
    private SyncService syncService;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void bpmnProcess() {
        // Synchronous service call with mocked service bean
        given(this.syncService.call()).willReturn("Hello Test");
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey("integrationTestProcess");
        assertThat(processEngine.getRuntimeService().getVariable(processInstance.getProcessInstanceId(), "greeting"))
            .isEqualTo("Hello Test");

        // Asynchronous service call with 'normal' bean
        Job asyncJob = processEngine.getManagementService().createJobQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(asyncJob).isNotNull();
        processEngine.getManagementService().executeJob(asyncJob.getId());
        assertThat(asyncService.getLastGreetingReceived()).isEqualTo("Hello Test");
    }

    public interface SyncService {

        String call();

    }

}
