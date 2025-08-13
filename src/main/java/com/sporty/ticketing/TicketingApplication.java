package com.sporty.ticketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Entry point for the Ticketing System application.
 */
@SpringBootApplication
@ConfigurationPropertiesScan // enables @ConfigurationProperties scanning
public class TicketingApplication {

    /**
     * Bootstraps the Spring application.
     * @param args application args
     */
    public static void main(String[] args) {
        SpringApplication.run(TicketingApplication.class, args);
    }
}
