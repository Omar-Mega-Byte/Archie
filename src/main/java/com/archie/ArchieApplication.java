package com.archie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Archie - Blueprint to Boot
 * AI-powered Spring Boot project generator from hand-drawn diagrams
 * 
 * @author Development Team
 * @version 1.0.0
 * @since 2025-12-24
 */
@SpringBootApplication
@EnableScheduling
public class ArchieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArchieApplication.class, args);
    }
}
