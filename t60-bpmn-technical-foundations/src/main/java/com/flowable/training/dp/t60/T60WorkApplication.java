package com.flowable.training.dp.t60;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = {
    FreeMarkerAutoConfiguration.class
})
public class T60WorkApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(T60WorkApplication.class, args);
    }
}

