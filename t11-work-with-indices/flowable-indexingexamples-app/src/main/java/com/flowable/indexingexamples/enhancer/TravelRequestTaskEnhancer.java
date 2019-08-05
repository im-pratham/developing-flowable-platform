package com.flowable.indexingexamples.enhancer;

import com.fasterxml.jackson.databind.JsonNode;
import com.flowable.platform.service.task.TaskResultMapper;
import com.flowable.platform.service.task.TaskSearchRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.Map;

//@Component
public class TravelRequestTaskEnhancer implements TaskResultMapper.Enhancer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TravelRequestTaskEnhancer.class);

    @Override
    public void enhance(TaskSearchRepresentation response, JsonNode indexedData) {

        JsonNode scopeDefinitionKey = indexedData.get("scopeDefinitionKey");
        if (scopeDefinitionKey != null && scopeDefinitionKey.asText().equals("travelRequest")) {

            TaskSearchRepresentation myCustomResponse = new TaskSearchRepresentation();

            Map<String, Object> myVariables = response.getVariables();

            BeanUtils.copyProperties(myCustomResponse, response);

            LOGGER.info("Cleaning default response");

            response.setVariables(myVariables);

            LOGGER.info("Setting travel request variables");
        }
    }
}