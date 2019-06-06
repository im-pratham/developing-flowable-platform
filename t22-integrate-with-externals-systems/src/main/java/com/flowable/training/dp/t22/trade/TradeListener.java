package com.flowable.training.dp.t22.trade;

import static com.flowable.training.dp.t22.T22WorkApplication.EXECUTED_ORDER_QUEUE;

import java.util.Collections;

import org.flowable.engine.RuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Notifies the waiting process of the executed trade order
 */
@Component
public class TradeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeListener.class);

    private final RuntimeService runtimeService;

    public TradeListener(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @JmsListener(destination = EXECUTED_ORDER_QUEUE)
    public void receiveMessage(@Payload Order order) {
        LOGGER.info("Order '{}' executed, triggering process", order);
        runtimeService.triggerAsync(
            runtimeService.createExecutionQuery().activityId("receivetask1")
                .processInstanceId(order.getProcessInstanceId()).singleResult().getId(),
            Collections.emptyMap());
    }

}
