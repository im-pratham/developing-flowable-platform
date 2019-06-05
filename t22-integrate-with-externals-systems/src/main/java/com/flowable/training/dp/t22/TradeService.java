package com.flowable.training.dp.t22;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TradeService implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(FraudDetectionService.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final RuntimeService runtimeService;

    public TradeService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        executor.submit(() -> {
            LOGGER.info("Executing trade");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            LOGGER.info("Trade executed");
            runtimeService.trigger(
                runtimeService.createExecutionQuery().activityId("receivetask1").processInstanceId(execution.getProcessInstanceId()).singleResult().getId(),
                Collections.emptyMap());
        });
    }

}
