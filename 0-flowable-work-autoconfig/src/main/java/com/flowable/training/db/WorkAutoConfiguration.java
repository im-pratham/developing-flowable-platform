package com.flowable.training.db;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ SecurityConfiguration.class, SecurityActuatorConfiguration.class })
public class WorkAutoConfiguration {


}
