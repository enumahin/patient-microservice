package com.alienworkspace.cdr.patient;

import com.alienworkspace.cdr.patient.config.AppConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for the Demographic module.
 * This is the entry point of the Spring Boot application.
 *
 * <p>The application is configured with {@link SpringBootApplication}, which
 * enables auto-configuration, component scanning, and configuration properties support.</p>
 *
 * <p>To run the application, execute the {@link #main(String[])} method.</p>
 *
 * <p>Author: Codeium Engineering Team</p>
 */
@SpringBootApplication
@EnableConfigurationProperties(value = {AppConfig.class})
@EnableFeignClients
public class PatientApplication {

    /**
     * The main entry point of the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PatientApplication.class, args);
    }

}
