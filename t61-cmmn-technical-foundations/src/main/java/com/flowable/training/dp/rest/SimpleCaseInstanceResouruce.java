package com.flowable.training.dp.rest;

import java.util.List;

import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.common.rest.api.DataResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimpleCaseInstanceResouruce {

    private final CmmnRuntimeService cmmnRuntimeService;

    public SimpleCaseInstanceResouruce(CmmnRuntimeService cmmnRuntimeService) {
        this.cmmnRuntimeService = cmmnRuntimeService;
    }

    @GetMapping("/api/training/case-instances-with-starter/{starter}")
    public DataResponse<CaseInstance> getCaseInstancesWithStarter(@PathVariable String starter) {
        List<CaseInstance> caseInstances = cmmnRuntimeService.createCaseInstanceQuery()
            .caseInstanceStartedBy(starter)
            .list();

        DataResponse<CaseInstance> caseInstanceDataResponse = new DataResponse<>();
        caseInstanceDataResponse.setData(caseInstances);
        caseInstanceDataResponse.setStart(0);
        caseInstanceDataResponse.setTotal(caseInstances.size());
        caseInstanceDataResponse.setOrder("asc");
        caseInstanceDataResponse.setSort("creationTime");

        return caseInstanceDataResponse;
    }

}
