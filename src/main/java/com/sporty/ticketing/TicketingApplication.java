package com.sporty.ticketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Main entry point for the Ticketing System application.
 * <p>
 * This class bootstraps the Spring Boot application and enables scanning
 * for configuration properties via {@link ConfigurationPropertiesScan}.
 * </p>
 */
@SpringBootApplication
@ConfigurationPropertiesScan // Enables @ConfigurationProperties scanning
public class TicketingApplication {

    /**
     * Starts the Ticketing System application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(TicketingApplication.class, args);
    }
}
