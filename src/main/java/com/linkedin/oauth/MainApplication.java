package com.linkedin.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;


/*
 * Create Spring Boot Application and set a default controller
 */

@SpringBootApplication
@ComponentScan(basePackages = {"com.linkedin.oauth", "com.linkedin.oauth.*"})
@EnableScheduling
public class MainApplication {
    public MainApplication() {
    }

    public static void main(final String[] args) {
        SpringApplication.run(MainApplication.class, args);

    }

    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder builder) {
        return builder.build();
    }
}
