package com.flowable.indexingexamples.enhancer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flowable.indexing.api.IndexingManagerHelper;
import com.flowable.indexing.impl.IndexedDataEnhancerAdapter;
import com.flowable.indexingexamples.model.Country;
import com.flowable.indexingexamples.service.GeoMapCountryService;
import org.flowable.cmmn.engine.impl.persistence.entity.CaseInstanceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.flowable.indexing.api.IndexingJsonConstants.CREATED_VARIABLES;
import static com.flowable.indexing.api.IndexingJsonConstants.FIELD_VARIABLE_NAME;

@Component
public class CustomEmployeeIndexedDataEnhancer extends IndexedDataEnhancerAdapter {

    public static final String TEXT_VALUE = "textValue";
    public static final String EXTRACTED_ORIGIN = "extractedOrigin";
    public static final String EXTRACTED_DESTINATION = "extractedDestination";
    public static final String ORIGIN_GEO_POINT = "originGeoPoint";
    public static final String DESTINATION_GEO_POINT = "destinationGeoPoint";

    @Autowired
    public GeoMapCountryService geoMapCountryService;

    @Override
    public void enhanceCaseInstanceEndData(CaseInstanceEntity caseInstanceEntity, ObjectNode data, IndexingManagerHelper indexingManagerHelper) {
        if (data.has(CREATED_VARIABLES)) {
            JsonNode createdVariables = data.get(CREATED_VARIABLES);
            if (!createdVariables.isNull() && createdVariables.size() > 0) {
                for (JsonNode variableNode : createdVariables) {
                    if (EXTRACTED_ORIGIN.equals(variableNode.get(FIELD_VARIABLE_NAME).asText())) {
                        JsonNode geopointNode = getJsonNode(variableNode);

                        data.replace(ORIGIN_GEO_POINT, geopointNode);
                    } else if (EXTRACTED_DESTINATION.equals(variableNode.get(FIELD_VARIABLE_NAME).asText())) {
                        JsonNode geopointNode = getJsonNode(variableNode);

                        data.replace(DESTINATION_GEO_POINT, geopointNode);
                    }
                }
            }
        }

    }

    private JsonNode getJsonNode(JsonNode variableNode) {
        String countryCode = variableNode.get(TEXT_VALUE).asText();
        Map<String, Double> geopoint = geoMapCountryService.countryToGeoMap(new Country(countryCode));

        return new ObjectMapper().convertValue(geopoint, JsonNode.class);
    }
}
