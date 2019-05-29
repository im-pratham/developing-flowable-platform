package com.flowable.training.dp.t56;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionLoggerListener implements ExecutionListener {

    private static final long serialVersionUID = -8776403704563659055L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionLoggerListener.class);

    @Override
    public void notify(DelegateExecution execution) {
        LOGGER.debug("Executed the following activity: {}", execution.getCurrentFlowElement().getId());
    }

}
