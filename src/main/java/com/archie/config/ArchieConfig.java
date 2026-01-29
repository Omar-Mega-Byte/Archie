package com.archie.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Application configuration properties
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "archie")
public class ArchieConfig {

    private Upload upload = new Upload();
    private Generation generation = new Generation();

    @Data
    public static class Upload {
        private String directory;
    }

    @Data
    public static class Generation {
        private String basePackage;
        private String outputDirectory;
    }
}
