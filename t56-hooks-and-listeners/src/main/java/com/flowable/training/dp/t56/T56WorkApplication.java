package com.flowable.training.dp.t56;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;

import com.flowable.training.dp.TrainingBaseApplication;

@SpringBootApplication(exclude = {
    FreeMarkerAutoConfiguration.class
})

public class T56WorkApplication extends TrainingBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(T56WorkApplication.class, args);
    }
}

