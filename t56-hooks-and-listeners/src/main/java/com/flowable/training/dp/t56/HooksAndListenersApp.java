package com.flowable.training.dp.t56;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;

import com.flowable.training.dp.WorkApplication;

@SpringBootApplication(exclude = {
    FreeMarkerAutoConfiguration.class
})
public class HooksAndListenersApp extends WorkApplication {

    public static void main(String[] args) {
        SpringApplication.run(HooksAndListenersApp.class, args);
    }

}
