package com.flowable.training.dp.config;

import com.flowable.spring.boot.EngineConfigurationConfigurer;
import com.flowable.training.dp.listeners.CustomListener;
import com.google.common.collect.ImmutableList;
import org.flowable.engine.cfg.HttpClientConfig;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomProcessEngineConfig  {

    @Value("${http.connection.connectTimeout:5000}")
    private int connectTimeout;

    @Value("${http.connection.socketTimeout:5000}")
    private int socketTimeout;

    @Value("${http.connection.connectionRequestTimeout:5000}")
    private int connectionRequestTimeout;

    @Value("${http.connection.requestRetryLimit:3}")
    private int requestRetryLimit;

    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> customProcessEngineConfigurer() {
        return engineConfiguration -> {
            engineConfiguration.setEventListeners(ImmutableList.of(new CustomListener()));

            HttpClientConfig client = new HttpClientConfig();
            client.setConnectTimeout(connectTimeout);
            client.setSocketTimeout(socketTimeout);
            client.setConnectionRequestTimeout(connectionRequestTimeout);
            client.setRequestRetryLimit(requestRetryLimit);
            engineConfiguration.setHttpClientConfig(client);
        };
    }

}
