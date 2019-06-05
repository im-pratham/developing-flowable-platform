package com.flowable.training.dp.t32;

import static org.assertj.core.api.Assertions.assertThat;

import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.engine.CmmnEngine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = { T32WorkApplication.class })
@TestPropertySource("/application-test.properties")
public class CmmnIntegrationTest {

    /**
     * Any Engine and any service can be injected here
     */
    @Autowired
    private CmmnEngine cmmnEngine;

    @Test
    public void cmmnCase() {
        // Start case
        CaseInstance caseInstance = cmmnEngine.getCmmnRuntimeService().createCaseInstanceBuilder()
            .caseDefinitionKey("test-case")
            .start();

        // Assert that we have one plan item in status 'enabled' and no task
        PlanItemInstance planItemInstance = cmmnEngine.getCmmnRuntimeService().createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
            .singleResult();
        assertThat(planItemInstance.getState()).isEqualTo("enabled");
        assertThat(cmmnEngine.getCmmnTaskService().createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult()).isNull();

        // Do manual activation
        cmmnEngine.getCmmnRuntimeService().createPlanItemInstanceTransitionBuilder(planItemInstance.getId()).start();

        // Assert that we have one plan item in status 'active' and a task
        planItemInstance = cmmnEngine.getCmmnRuntimeService().createPlanItemInstanceQuery().caseInstanceId(caseInstance.getId())
            .singleResult();
        assertThat(planItemInstance.getState()).isEqualTo("active");
        assertThat(cmmnEngine.getCmmnTaskService().createTaskQuery().caseInstanceId(caseInstance.getId()).singleResult()).isNotNull();
    }

}
