package com.flowable.training.dp.t22.trade;

import static com.flowable.training.dp.t22.T22WorkApplication.EXECUTED_ORDER_QUEUE;
import static com.flowable.training.dp.t22.T22WorkApplication.ORDER_QUEUE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class TradeExecutor {

    private final Logger LOGGER = LoggerFactory.getLogger(TradeExecutor.class);

    private final JmsTemplate jmsTemplate;

    public TradeExecutor(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @JmsListener(destination = ORDER_QUEUE)
    public void receiveMessage(@Payload Order order) throws InterruptedException {
        LOGGER.info("Executing order: '{}'", order);
        Thread.sleep(5000);
        jmsTemplate.convertAndSend(EXECUTED_ORDER_QUEUE, order);
    }

}
