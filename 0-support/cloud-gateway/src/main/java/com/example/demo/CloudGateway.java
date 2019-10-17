package com.example.demo;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@SpringBootApplication
public class CloudGateway {

	public static void main(String[] args) {
		SpringApplication.run(CloudGateway.class, args);
	}

	@Bean
	public CorsWebFilter corsWebFilter() {
		return new CorsWebFilter(corsConfigurationSource());
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfig = new CorsConfiguration().applyPermitDefaultValues();
		corsConfig.addAllowedMethod(HttpMethod.GET);
		corsConfig.addAllowedMethod(HttpMethod.OPTIONS);
		corsConfig.setAllowCredentials(true);
		corsConfig.setAllowedOrigins(Collections.singletonList("*"));
		source.registerCorsConfiguration("/**", corsConfig);
		return source;
	}

	/**
	 * http://localhost:8042/swapi/api/people/?format=json
	 */
	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
			.route("swapi", p -> p
				.path("/swapi/**")
				.filters(fn -> fn.stripPrefix(1).dedupeResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "RETAIN_FIRST"))
				.uri("https://swapi.co"))
			.build();
	}

}
