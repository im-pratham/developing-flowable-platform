package com.flowable.training.dp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

import com.flowable.design.conf.ApplicationConfiguration;
import com.flowable.design.servlet.AppDispatcherServletConfiguration;

@Import({
	ApplicationConfiguration.class,
    AppDispatcherServletConfiguration.class
})
@SpringBootApplication
public class DevelopingFlowablePlatformDesignApp {

    public static void main(String[] args) {
        SpringApplication.run(DevelopingFlowablePlatformDesignApp.class, args);
    }
}
