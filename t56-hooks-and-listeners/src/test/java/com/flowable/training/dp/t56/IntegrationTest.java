package com.flowable.training.dp.t56;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { T56WorkApplication.class })
public class IntegrationTest {

    @Autowired
    private ProcessEngine processEngine;

    @Test
    public void startHooksAndListenersProcess() {
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceByKey("hooks-and-listeners-process");
    }

}
