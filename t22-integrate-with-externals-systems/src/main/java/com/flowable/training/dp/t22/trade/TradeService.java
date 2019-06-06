package com.flowable.training.dp.t22.trade;

import static com.flowable.training.dp.t22.T22WorkApplication.ORDER_QUEUE;

import java.math.BigDecimal;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class TradeService implements JavaDelegate {

    private final JmsTemplate jmsTemplate;

    public TradeService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) {
        String symbol = (String) execution.getVariable("symbol");
        BigDecimal amount = new BigDecimal(String.valueOf(execution.getVariable("amount")));

        jmsTemplate.convertAndSend(ORDER_QUEUE, new Order(execution.getProcessInstanceId(), amount, symbol));
    }

}
