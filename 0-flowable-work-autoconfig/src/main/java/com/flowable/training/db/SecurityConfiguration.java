package com.flowable.training.db;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

import com.flowable.core.spring.security.web.authentication.AjaxAuthenticationFailureHandler;
import com.flowable.core.spring.security.web.authentication.AjaxAuthenticationSuccessHandler;
import com.flowable.platform.common.security.SecurityConstants;

@Configuration
@Order(10)
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    protected ObjectProvider<RememberMeServices> rememberMeServicesObjectProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        RememberMeServices rememberMeServices = rememberMeServicesObjectProvider.getIfAvailable();
        String key = null;
        if (rememberMeServices instanceof AbstractRememberMeServices) {
            key = ((AbstractRememberMeServices) rememberMeServices).getKey();
        }
        http
            .csrf().disable()
            // Frontend would eventually load browser's PDF viewer using object/embed
            // To avoid problems in some scenarios, we need to do set X-Frame-Options sameorigin;
            .headers().frameOptions().sameOrigin()

            .and()
            .rememberMe()
            .key(key)
            .rememberMeServices(rememberMeServicesObjectProvider.getIfAvailable())
            .and()
            .logout()
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
            .logoutUrl("/auth/logout")
            .and()
            // Non authenticated exception handling. The formLogin and httpBasic configure the exceptionHandling
            // We have to initialize the exception handling with a default authentication entry point in order to return 401 each time and not have a
            // forward due to the formLogin or the HTTP basic popup due to the httpBasic
            .exceptionHandling()
            .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), AnyRequestMatcher.INSTANCE)
            .and()
            .formLogin()
            .loginProcessingUrl("/auth/login")
            .successHandler(new AjaxAuthenticationSuccessHandler())
            .failureHandler(new AjaxAuthenticationFailureHandler())
            .and()
            .authorizeRequests()
            .antMatchers("/analytics-api/**").hasAuthority(SecurityConstants.ACCESS_REPORTS_METRICS)
            .antMatchers("/template-api/**").hasAuthority(SecurityConstants.ACCESS_TEMPLATE_MANAGEMENT)
            .antMatchers("/work-object-api/**").hasAuthority(SecurityConstants.ACCESS_WORKOBJECT_API)
            // allow context root for all (it triggers the loading of the initial page)
            .antMatchers("/") .permitAll()
            .antMatchers(
                "/**/*.svg", "/**/*.ico", "/**/*.png", "/**/*.woff2", "/**/*.css",
                "/**/*.woff", "/**/*.html", "/**/*.js",
                "/**/index.html").permitAll()
            .anyRequest().authenticated()
            .and()
            .httpBasic();
    }
}
