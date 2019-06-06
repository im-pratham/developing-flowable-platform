package com.flowable.training.dp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = {
    FreeMarkerAutoConfiguration.class
})
public class T61WorkApplication {

    public static void main(String[] args) {
        SpringApplication.run(T61WorkApplication.class, args);
    }


}
