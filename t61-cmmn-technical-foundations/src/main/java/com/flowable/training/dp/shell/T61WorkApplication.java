package com.flowable.training.dp.shell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;

@SpringBootApplication(exclude = {
    FreeMarkerAutoConfiguration.class
})
public class T61WorkApplication {

    public static void main(String[] args) {
        SpringApplication.run(T61WorkApplication.class, args);
    }


}
