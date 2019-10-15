package com.flowable.training.dp.service.impl;

import java.util.Collections;
import java.util.List;

import org.flowable.common.engine.api.scope.ScopeTypes;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flowable.audit.api.AuditService;
import com.flowable.audit.api.runtime.AuditInstance;

@Service
public class HelloWorldServiceAgain {

    private final RuntimeService runtimeService;
    public HelloWorldServiceAgain(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public void greet(String greeter, String personToBeGreeted) {
        // Get all running process instances

        ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
            .processDefinitionKey("dimitrios_process")
            .variable("initiator", "dimitrios")
            .start();

        System.out.println("new process: " + processInstance.getId());

        System.out.println(greeter + " says: Hello " + personToBeGreeted + ", I also created a new process.");


        auditService.createAuditInstanceBuilder()
            .scopeType(ScopeTypes.CMMN)
            .scopeId("CAS-ASDASD-12314-123123")
            .payload(Collections.singletonMap("text", "Case was very interesting"))
            .create();

        List<AuditInstance> list = auditService.createAuditInstanceQuery()
            .scopeType(ScopeTypes.CMMN)
            .id("CAS-ASDASD-12314-123123")
            .list();


    }

    @Autowired
    AuditService auditService;


}
