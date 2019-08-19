package com.flowable.training.indexing.elasticsearch;

import static com.flowable.indexing.api.IndexingJsonConstants.CREATED_VARIABLES;
import static com.flowable.indexing.api.IndexingJsonConstants.FIELD_VARIABLES;
import static com.flowable.indexing.api.IndexingJsonConstants.FIELD_VARIABLE_NAME;

import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.service.impl.persistence.entity.VariableInstanceEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flowable.indexing.api.IndexingManagerHelper;
import com.flowable.indexing.impl.IndexedDataEnhancerAdapter;

@Component
public class CustomIndexedDataEnhancerAdapter extends IndexedDataEnhancerAdapter {

    public static final String TEXT_VALUE = "textValue";
    public static final String EXTRACTED_TEXT_FROM_A_SUB_FORM = "extractedTextFromASubForm";
    public static final String EXTRACTED_TEXT_FROM_A_SUB_FORM_IN_UPPERCASE_AS_A_FIELD = "extractedTextFromASubFormInUppercaseAsAField";

    @Override
    public void enhanceVariableCreateData(VariableInstanceEntity variable, String scopeId, String scopeType, String scopeHierarchyType, ObjectNode data, IndexingManagerHelper indexingManagerHelper) {
        extractFieldFromVariables(data, CREATED_VARIABLES);
    }


    @Override
    public void enhanceTaskReindexData(HistoricTaskInstance historicTaskInstance, ObjectNode data, IndexingManagerHelper indexingManagerHelper) {
        extractFieldFromVariables(data, FIELD_VARIABLES);
    }

    private void extractFieldFromVariables(ObjectNode data, String variablesField) {
        extractFieldFromVariables(data, variablesField, EXTRACTED_TEXT_FROM_A_SUB_FORM, EXTRACTED_TEXT_FROM_A_SUB_FORM_IN_UPPERCASE_AS_A_FIELD);
    }

    private void extractFieldFromVariables(ObjectNode data, String variablesField, String variableName, String targetField) {
        if (data.has(variablesField)) {
            JsonNode createdVariables = data.get(variablesField);
            if (!createdVariables.isNull() && createdVariables.size() > 0) {
                for (JsonNode variableNode : createdVariables) {
                    if (variableName.equals(variableNode.get(FIELD_VARIABLE_NAME).asText())) {
                        data.put(targetField, variableNode.get(TEXT_VALUE).asText().toUpperCase());
                    }
                }
            }
        }
    }

}
