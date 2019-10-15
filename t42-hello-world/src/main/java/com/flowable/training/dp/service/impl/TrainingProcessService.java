package com.flowable.training.dp.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;

public class TrainingProcessService {

    // Write the following methods:
    /*
        -> returns all active process instances of with country == 'ch'
        -> start a new process 'country_process' with the variable 'country' as an initialized var.
        -> returns the last historic process instance
        -> HARD MODE: All activities that have been passed in a certain process instance
        -> Get the creation Date of the first version of a specified process definition (by key)

        Call all of them in a process

        Runtime
        Repository
        History

      */
    private final RuntimeService runtimeService;
    private final HistoryService historyService;

    public TrainingProcessService(RuntimeService runtimeService, HistoryService historyService) {
        this.runtimeService = runtimeService;
        this.historyService = historyService;
    }

    public List<ProcessInstance> getAllProcessInstancesWithCountryCh() {
        return runtimeService.createProcessInstanceQuery()
            .variableValueEquals("country", "ch")
            .active()
            .list();
    }

    public void startCountryProcess(String country) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceBuilder()
            .processDefinitionKey("country_process")
            .variable("country", country)
            .start();

        System.out.println("created new PI: " + processInstance.getId());
    }

    public String getLastHistoricProcessInstance() {
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery()
            .orderByProcessInstanceEndTime()
            .asc()
            .list();
        return null;
    }

    public List<String> getAllPassedActivities(String processInstanceId) {
        List<String> allActivities = runtimeService.createActivityInstanceQuery()
            .processInstanceId(processInstanceId)
            .finished()
            .list()
            .stream()
            .map(ActivityInstance::getActivityName)
            .collect(Collectors.toList());

        return allActivities;
    }
}
