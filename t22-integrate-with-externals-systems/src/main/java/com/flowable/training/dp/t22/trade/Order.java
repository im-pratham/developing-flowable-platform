package com.flowable.training.dp.t22.trade;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.StringJoiner;

public class Order implements Serializable {

    private static final long serialVersionUID = 6364080461184259540L;

    private String processInstanceId;
    private BigDecimal amount;
    private String symbol;

    public Order() {
    }

    public Order(String processInstanceId, BigDecimal amount, String symbol) {
        this.processInstanceId = processInstanceId;
        this.amount = amount;
        this.symbol = symbol;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }
    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    @Override
    public String toString() {
        return new StringJoiner(", ", Order.class.getSimpleName() + "[", "]")
            .add("processInstanceId='" + processInstanceId + "'")
            .add("amount=" + amount)
            .add("symbol='" + symbol + "'")
            .toString();
    }
}
