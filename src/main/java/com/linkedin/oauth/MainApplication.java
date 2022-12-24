package com.linkedin.oauth;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;


/*
 * Create Spring Boot Application and set a default controller
 */

@SpringBootApplication
@ComponentScan(basePackages = {"com.linkedin.oauth","com.linkedin.oauth.*"})
@EnableScheduling
public class MainApplication {
    public MainApplication() { }
    public static void main(final String[] args) {
        SpringApplication.run(MainApplication.class, args);

    }

}
