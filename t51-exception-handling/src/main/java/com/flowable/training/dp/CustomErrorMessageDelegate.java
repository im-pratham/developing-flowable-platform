package com.flowable.training.dp;

import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * A Java Delegate that demonstrates how to display a custom error message.
 */
@Component("customErrorMessageDelegate")
public class CustomErrorMessageDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        // Some business logic here
        // ...
        // ...
        String initiator = (String) execution.getVariable("initiator");
        int amount = (int) execution.getVariable("amount");
        if(amount < 1000) {
            throw new FlowableException("Amount is only " + amount + ". Have a nice day, " + initiator + ".");
        }

    }
}
