package com.flowable.training.dp.t22;

import java.io.IOException;
import java.util.stream.Stream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

@SpringBootApplication(exclude = {
    FreeMarkerAutoConfiguration.class
})
@EnableWebMvc
@EnableScheduling
public class T22WorkApplication extends SpringBootServletInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(T22WorkApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(T22WorkApplication.class, args);
    }

    @Autowired
    private GreenMail greenMail;

    @Bean
    public GreenMail greenMail() {
        ServerSetup serverSetup = new ServerSetup(2525, null, "smtp");
        GreenMail smtp = new GreenMail(serverSetup);
        smtp.start();
        return smtp;
    }

    @Scheduled(fixedRate = 1000)
    public void logMessages() {
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

        if (receivedMessages.length == 0) {
            return;
        }

        Stream.of(receivedMessages).forEach(mime -> {
            try {
                LOGGER.info("Sent email '{}' from {} to {}", mime.getContent(), mime.getFrom(), mime.getAllRecipients());
            } catch (IOException | MessagingException e) {
                throw new RuntimeException(e);
            }
        });

        greenMail.reset();
    }

    @Configuration
    @Order(7)
    public class ApiUserSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            String apiUrl = "/api/**";
            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry httpInBuild = http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .antMatcher(apiUrl)
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, apiUrl).permitAll(); // To allow for swagger's unauthorized OPTIONS requests

                httpInBuild.antMatchers(apiUrl).permitAll();
            }

        }

}
