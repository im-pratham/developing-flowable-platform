package com.flowable.training.dp.shell;

import java.util.List;

import org.flowable.cmmn.api.CmmnHistoryService;
import org.flowable.cmmn.api.history.HistoricCaseInstance;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@ShellCommandGroup("CMMN History Service")
public class CmmnHistoryServiceCommands {

    private final CmmnHistoryService cmmnHistoryService;

    public CmmnHistoryServiceCommands(CmmnHistoryService cmmnHistoryService) {
        this.cmmnHistoryService = cmmnHistoryService;
    }

    @ShellMethod(value = "List cases", key = "list-historic-cases")
    public String listCases(String caseDefinitionKey) {
        StringBuilder message = new StringBuilder();
        List<HistoricCaseInstance> historicCaseInstances = cmmnHistoryService.createHistoricCaseInstanceQuery()
            .caseDefinitionKey(caseDefinitionKey)
            .list();

        for (HistoricCaseInstance historicCaseInstance : historicCaseInstances) {
            message.append("ID: " + historicCaseInstance.getId())
                .append(", created: ")
                .append(historicCaseInstance.getStartTime())
                .append(", completed: ")
                .append(historicCaseInstance.getEndTime())
                .append("\n");
        }
        return message.toString();
    }

}
