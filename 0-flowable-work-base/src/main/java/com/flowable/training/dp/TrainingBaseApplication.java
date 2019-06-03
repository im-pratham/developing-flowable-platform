package com.flowable.training.dp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(exclude = {
    FreeMarkerAutoConfiguration.class
})
public class TrainingBaseApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(TrainingBaseApplication.class, args);
    }
}
