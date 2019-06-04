package com.flowable.training.dp.service.impl;

import org.flowable.common.engine.api.FlowableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flowable.core.idm.api.PlatformIdentityService;
import com.flowable.core.idm.api.PlatformUser;

/**
 * Service to be called by the Hello World Process
 */
@Transactional
@Service("helloWorldService")
public class HelloWorldService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldService.class);


    private final PlatformIdentityService platformIdentityService;

    /**
     * Inject the dependencies we need
     * @param platformIdentityService The Platform Identity Service to fetch user information
     */
    public HelloWorldService(PlatformIdentityService platformIdentityService) {
        this.platformIdentityService = platformIdentityService;
    }

    /**
     * Simple Greeter that will log a simple "Hello World".
      * @param greeterId The ID of the greeter
     * @param personToBeGreetedId The ID of the user to be greeted
     */
    public void simpleGreet(String greeterId, String personToBeGreetedId) {
        LOGGER.warn(greeterId + " says: Hello " + personToBeGreetedId);
    }

    /**
     * A more intricate way to say hello to the world which also gets the greeter's and gretee's display name.
     * @param greeterId The ID of the greeter
     * @param personToBeGreetedId The ID of the user to be greeted
     */
    public void advancedGreet(String greeterId, String personToBeGreetedId) {
        PlatformUser greeter = platformIdentityService.createPlatformUserQuery().userId(greeterId).singleResult();
        if(greeter == null) {
            throw new FlowableException("Greeter " + greeterId + " does not exist.");
        }

        PlatformUser personToBeGreeted = platformIdentityService.createPlatformUserQuery().userId(personToBeGreetedId).singleResult();
        if(personToBeGreeted == null) {
            throw new FlowableException("Gretee " + personToBeGreetedId + " does not exist.");
        }

        LOGGER.warn(greeter.getDisplayName() + " says: Hello " + personToBeGreeted.getDisplayName());
    }

}
