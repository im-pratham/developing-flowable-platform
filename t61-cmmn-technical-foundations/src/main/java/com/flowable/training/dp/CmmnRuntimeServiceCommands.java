package com.flowable.training.dp;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.repository.CmmnDeployment;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstanceQuery;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.ClassPathResource;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@ShellCommandGroup("Repository Service")
public class CmmnRuntimeServiceCommands {

    private final CmmnRuntimeService cmmnRuntimeService;

    public CmmnRuntimeServiceCommands(CmmnRuntimeService cmmnRuntimeService) {
        this.cmmnRuntimeService = cmmnRuntimeService;
    }

    @ShellMethod(value = "Start Case", key = "start-case")
    public String startCase(String caseDefinitionKey, @ShellOption(defaultValue="no-key") String businessKey) {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder()
            .businessKey(businessKey)
            .caseDefinitionKey(caseDefinitionKey)
            .start();

        return "Successfully created new case instance with ID " + caseInstance.getId();
    }

    public String listCases(String caseDefinitionKey) {
        StringBuilder message = new StringBuilder();
        List<CaseInstance> caseInstances = cmmnRuntimeService.createCaseInstanceQuery()
            .caseDefinitionKey(caseDefinitionKey)
            .list();

        for (CaseInstance caseInstance : caseInstances) {
            message.append("ID: " + caseInstance.getId())
                .append(", created: ")
                .append(caseInstance.getStartTime())
                .append("\n");
        }
        return message.toString();
    }


    @ShellMethod(value = "Display Plan Items", key="display-plan-items")
    public String displayPlanItems(String caseInstanceId, @ShellOption(defaultValue = "") String state) {
        StringBuilder message = new StringBuilder();

        PlanItemInstanceQuery planItemInstanceQuery = cmmnRuntimeService.createPlanItemInstanceQuery()
            .caseInstanceId(caseInstanceId);

        if(!state.isEmpty()) {
            planItemInstanceQuery.planItemInstanceState(state);
        }

        List<PlanItemInstance> planItems = planItemInstanceQuery.list();

        for (PlanItemInstance planItem : planItems) {
            message.append("ID:").append(planItem.getId())
                .append(", name: ").append(planItem.getName())
                .append(", created:")
                .append(planItem.getCreateTime())
                .append("\n");
        }
        return message.toString();

    }

    @ShellMethod(value = "Display variables", key = "display-case-variables")
    public String displayVariables(String caseInstanceId) {
        StringBuilder message = new StringBuilder();

        Map<String, Object> variables = cmmnRuntimeService.getVariables(caseInstanceId);
        for (Map.Entry<String, Object> variableSet : variables.entrySet()) {
            message.append(variableSet.getKey())
                .append(": ")
                .append(variableSet.getValue())
                .append("\n");
            ;
        }

        return message.toString();
    }

    @ShellMethod(value = "Display variable", key = "display-case-variable")
    public String displayVariable(String caseInstanceId, String variableName) {
        return cmmnRuntimeService.getVariable(caseInstanceId, variableName).toString();
    }

    @ShellMethod(value = "Set variable", key = "set-variable")
    public String setVariable(String caseInstanceId, String variableName, Object value) {
        cmmnRuntimeService.setVariable(caseInstanceId, variableName, value);
        return "Set variable " + variableName + " to value " + value;
    }


}
