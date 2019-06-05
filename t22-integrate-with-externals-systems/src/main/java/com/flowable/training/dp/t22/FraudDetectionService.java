package com.flowable.training.dp.t22;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.impl.delegate.TriggerableActivityBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FraudDetectionService implements TriggerableActivityBehavior, JavaDelegate {

    private static final long serialVersionUID = 7560924420457856490L;

    private static final Logger LOGGER = LoggerFactory.getLogger(FraudDetectionService.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final RuntimeService runtimeService;
    private final ObjectMapper objectMapper;

    public FraudDetectionService(RuntimeService runtimeService, ObjectMapper objectMapper) {
        this.runtimeService = runtimeService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void trigger(DelegateExecution execution, String signalEvent, Object signalData) {
        LOGGER.info("Fraud detection executed");
    }

    @Override
    public void execute(DelegateExecution execution) {
        String symbol = (String) execution.getVariable("symbol");
        BigDecimal amount = new BigDecimal(String.valueOf(execution.getVariable("amount")));
        executor.submit(() -> {
            LOGGER.info("Executing fraud detection for order of amount {} on symbol {}", amount, symbol);
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            boolean fraud = amount.compareTo(new BigDecimal("50")) < 0 || amount.compareTo(new BigDecimal("10000")) > 0;
            runtimeService.trigger(execution.getId(), Collections.singletonMap("fraud", fraud));
        });
    }

}
