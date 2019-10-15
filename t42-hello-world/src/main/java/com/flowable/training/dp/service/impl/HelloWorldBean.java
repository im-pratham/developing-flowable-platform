package com.flowable.training.dp.service.impl;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

@Service
public class HelloWorldBean implements JavaDelegate {

    public void greet(String greeter, String personToBeGreeted) {
        System.out.println(greeter + " says: Hello " + personToBeGreeted );
    }

    @Override
    public void execute(DelegateExecution execution) {

    }
}
