package com.flowable.training.dp.shell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.dmn.api.DmnDecisionTable;
import org.flowable.dmn.api.DmnRepositoryService;
import org.flowable.dmn.api.DmnRuleService;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@ShellCommandGroup("DMN Rule Task")
public class RuleExecutionCommand {

    private final DmnRuleService dmnRuleService;
    private final DmnRepositoryService dmnRepositoryService;

    public RuleExecutionCommand(DmnRuleService dmnRuleService, DmnRepositoryService dmnRepositoryService) {
        this.dmnRuleService = dmnRuleService;
        this.dmnRepositoryService = dmnRepositoryService;
    }

    @ShellMethod(value = "Execute rule", key = "execute-rule")
    public String executeRule(String decisionKey, String[] args) {
        DmnDecisionTable dmnDecisionTable = dmnRepositoryService.createDecisionTableQuery()
            .decisionTableKey(decisionKey)
            .latestVersion()
            .singleResult();

        Map<String, Object> variables = new HashMap<>();
        for (String arg : args) {
            variables.put(arg.split("[:;,]")[0], arg.split(";")[1]);
        }

        if(dmnDecisionTable != null) {
            List<Map<String, Object>> result = dmnRuleService.createExecuteDecisionBuilder()
                .decisionKey(decisionKey)
                .variables(variables)
                .execute();

            StringBuilder message = new StringBuilder("Successfully executed the rule with the following results: ");
            for (Map<String, Object> line : result) {
                for (Map.Entry<String, Object> cell : line.entrySet()) {
                    message
                        .append("| ")
                        .append(cell.getKey())
                        .append(": ")
                        .append(cell.getValue())
                        .append(" | ");
                }
            }
            return message.toString();
        } else {
            return "There is no decision table with the key " + decisionKey;
        }
    }

}
