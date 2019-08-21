package com.flowable.training.dp;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.cfg.HttpClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = {FreeMarkerAutoConfiguration.class})
public class T31ConfiguringFlowable extends SpringBootServletInitializer implements CommandLineRunner {

    private final static Logger log = LoggerFactory.getLogger(T31ConfiguringFlowable.class);


    private final ProcessEngine processEngine;

    public T31ConfiguringFlowable(@Qualifier("processEngine") ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    public static void main(String[] args) {
        SpringApplication.run(T31ConfiguringFlowable.class, args);
    }


    @Override
    public void run(String... args) {
        ProcessEngineConfiguration processEngineConfiguration = processEngine.getProcessEngineConfiguration();

        processEngineConfiguration.getEventListeners().forEach(eventListener -> log.info("Event listener configured: "
                + eventListener.toString()));

        log.info("The next log will show \"Event type: Test Event for the custom listener received.\".");
        processEngine.getRuntimeService().dispatchEvent(() -> () -> "Test Event for the custom listener");

        HttpClientConfig httpClientConfig = processEngineConfiguration.getHttpClientConfig();
        int socketTimeout = httpClientConfig.getSocketTimeout();
        int connectTimeout = httpClientConfig.getConnectTimeout();
        int connectionRequestTimeout = httpClientConfig.getConnectionRequestTimeout();
        int requestRetryLimit = httpClientConfig.getRequestRetryLimit();

        log.info("HTTP Client Configuration. Timeouts: socket->{}, connect->{}, connectionRequest->{}. " +
                "Request retry limit -> {}", socketTimeout, connectTimeout, connectionRequestTimeout, requestRetryLimit);
    }

}
