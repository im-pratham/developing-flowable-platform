package com.flowable.training.dp.t32;

import org.flowable.common.engine.api.variable.VariableContainer;
import org.springframework.stereotype.Service;

import com.flowable.platform.tasks.AbstractPlatformTask;
import com.flowable.platform.tasks.ExtensionElementsContainer;

@Service
public class AsyncService extends AbstractPlatformTask {

    private String lastGreetingReceived;

    @Override
    public synchronized void executeTask(VariableContainer variableContainer, ExtensionElementsContainer extensionElementsContainer) {
        lastGreetingReceived = (String) variableContainer.getVariable("greeting");
    }

    public String getLastGreetingReceived() {
        return lastGreetingReceived;
    }

}
