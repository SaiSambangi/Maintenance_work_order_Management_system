package com.maintenance.workorder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) Configuration.
 *
 * Why is CORS needed?
 * The React frontend runs on http://localhost:3000 (or 5173 for Vite).
 * The Spring Boot backend runs on http://localhost:8080.
 * By default, web browsers block JavaScript from calling APIs on a different
 * "origin" (domain + port). CORS tells the browser: "It's OK to allow
 * requests from http://localhost:3000 to this backend."
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow requests from React dev server (both CRA and Vite ports)
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",   // Create React App
                "http://localhost:5173"    // Vite
        ));

        // Allow these HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow these headers
        config.setAllowedHeaders(List.of("*"));

        // Allow cookies/credentials if needed
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this CORS configuration to all API endpoints
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}
