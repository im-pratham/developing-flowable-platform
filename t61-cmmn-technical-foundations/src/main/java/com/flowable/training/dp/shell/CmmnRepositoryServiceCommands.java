package com.flowable.training.dp.shell;

import java.io.IOException;
import java.util.List;

import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.repository.CaseDefinition;
import org.flowable.cmmn.api.repository.CmmnDeployment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@ShellCommandGroup("CMMN Repository Service")
public class CmmnRepositoryServiceCommands {

    private final CmmnRepositoryService cmmnRepositoryService;

    public CmmnRepositoryServiceCommands(CmmnRepositoryService repositoryService) {
        this.cmmnRepositoryService = repositoryService;
    }

    @ShellMethod(value = "Deploy Case Model", key = "deploy")
    public String deployProcess(String resourcePath) {
        StringBuilder message = new StringBuilder();
        ClassPathResource caseResource = new ClassPathResource(resourcePath);

        try {
            CmmnDeployment deployment = cmmnRepositoryService.createDeployment()
                .addInputStream(resourcePath, caseResource.getInputStream())
                .deploy();

            return "Successfully deployed!\n" + "ID: " + deployment.getId();


        } catch (IOException e) {
            e.printStackTrace();
            return "Error during deployment!";
        }
    }

    @ShellMethod(value = "List Deployments", key="list-deployments")
    public String displayDeployments() {
        StringBuilder message = new StringBuilder();

        List<CmmnDeployment> deployments = cmmnRepositoryService.createDeploymentQuery().list();

        for (CmmnDeployment deployment : deployments) {
            List<CaseDefinition> caseDefinitions = cmmnRepositoryService
                .createCaseDefinitionQuery()
                .deploymentId(deployment.getId())
                .orderByCaseDefinitionVersion()
                .asc()
                .list();

            message.append("ID: ").append(deployment.getId()).append("\n").append("Date: ").append(deployment.getDeploymentTime()).append("\n");

            for (CaseDefinition caseDefinition : caseDefinitions) {
                message.append("Case Definition: ")
                    .append(caseDefinition.getKey())
                    .append(", Version: ")
                    .append(caseDefinition.getVersion())
                    .append("\n");
            }
            message.append("----------------------------------------------------");
        }
        return message.toString();
    }

    @ShellMethod(value="Delete deployment", key = "delete-deployment")
    public String deleteDeployment(String deploymentId) {
        cmmnRepositoryService.deleteDeployment(deploymentId, true);
        return "Deleted deployment " + deploymentId;
    }


}
