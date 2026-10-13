package com.maintenance.workorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Maintenance Work Order Management System.
 *
 * @SpringBootApplication = @Configuration + @EnableAutoConfiguration + @ComponentScan
 * Spring Boot will auto-configure everything based on what's on the classpath.
 */
@SpringBootApplication
public class WorkorderApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkorderApplication.class, args);
    }
}
