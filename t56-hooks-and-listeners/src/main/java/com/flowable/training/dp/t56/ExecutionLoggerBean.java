package com.flowable.training.dp.t56;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.DelegateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExecutionLoggerBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionLoggerBean.class);

    public void logExecutionStart(DelegateExecution delegateExecution) {
        LOGGER.info("Executing {} with field value: {}", delegateExecution.getCurrentActivityId(), DelegateHelper.getField(delegateExecution, "fieldOne"));
    }

}
