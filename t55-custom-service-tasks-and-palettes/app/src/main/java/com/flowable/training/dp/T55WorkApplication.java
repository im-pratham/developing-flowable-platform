package com.flowable.training.dp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class T55WorkApplication extends TrainingBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(T55WorkApplication
            .class, args);
    }
}
