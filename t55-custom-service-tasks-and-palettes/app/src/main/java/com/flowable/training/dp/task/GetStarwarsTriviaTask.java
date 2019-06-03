package com.flowable.training.dp.task;

import java.util.List;

import org.flowable.common.engine.api.variable.VariableContainer;
import org.springframework.stereotype.Component;

import com.flowable.platform.tasks.AbstractPlatformTask;
import com.flowable.platform.tasks.ExtensionElementsContainer;
import com.flowable.training.dp.service.StarwarsTriviaService;

@Component("getStarwarsTriviaTask")
public class GetStarwarsTriviaTask extends AbstractPlatformTask {


    private final StarwarsTriviaService starwarsTriviaService;

    public GetStarwarsTriviaTask(StarwarsTriviaService starwarsTriviaService) {
        this.starwarsTriviaService = starwarsTriviaService;
    }

    @Override
    public void executeTask(VariableContainer variableContainer, ExtensionElementsContainer extensionElementsContainer) {
        // Fetch the content of the task by accessing the getStringExtensionElementValue method of the AbstractPlatformTask
        String targetVariable = getStringExtensionElementValue("targetVariable", extensionElementsContainer, variableContainer, "ERROR");
        String triviaType = getStringExtensionElementValue("triviaType", extensionElementsContainer, variableContainer, "ERROR");

        List results = starwarsTriviaService.getTriviaByType(triviaType);
        variableContainer.setVariable(targetVariable, results);
    }



}
