package com.georges.flowableworkindexing.elasticsearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.flowable.indexing.api.IndexingManagerHelper;
import com.flowable.indexing.impl.IndexedDataEnhancerAdapter;
import org.flowable.variable.service.impl.persistence.entity.VariableInstanceEntity;
import org.springframework.stereotype.Component;

import static com.flowable.indexing.api.IndexingJsonConstants.CREATED_VARIABLES;
import static com.flowable.indexing.api.IndexingJsonConstants.FIELD_VARIABLE_NAME;

@Component
public class CustomIndexedDataEnhancerAdapter extends IndexedDataEnhancerAdapter {

    public static final String TEXT_VALUE = "textValue";
    public static final String EXTRACTED_TEXT_FROM_A_SUB_FORM = "extractedTextFromASubForm";
    public static final String EXTRACTED_TEXT_FROM_A_SUB_FORM_IN_UPPERCASE_AS_A_FIELD = "extractedTextFromASubFormInUppercaseAsAField";

    @Override
    public void enhanceVariableCreateData(VariableInstanceEntity variable, String scopeId, String scopeType, String scopeHierarchyType, ObjectNode data, IndexingManagerHelper indexingManagerHelper) {
        if (data.has(CREATED_VARIABLES)) {
            JsonNode createdVariables = data.get(CREATED_VARIABLES);
            if (!createdVariables.isNull() && createdVariables.size() > 0) {
                for (JsonNode variableNode : createdVariables) {
                    if (EXTRACTED_TEXT_FROM_A_SUB_FORM.equals(variableNode.get(FIELD_VARIABLE_NAME).asText())) {
                        data.put(EXTRACTED_TEXT_FROM_A_SUB_FORM_IN_UPPERCASE_AS_A_FIELD, variableNode.get(TEXT_VALUE).asText().toUpperCase());
                    }
                }
            }
        }
        super.enhanceVariableCreateData(variable, scopeId, scopeType, scopeHierarchyType, data, indexingManagerHelper);
    }

}
