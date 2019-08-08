package com.georges.flowableworkindexing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = {FreeMarkerAutoConfiguration.class})
public class GeorgesFlowableWorkIndexingApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(GeorgesFlowableWorkIndexingApplication.class, args);
    }
}
