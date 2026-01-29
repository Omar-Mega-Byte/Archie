package com.archie.codegen.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Result of code generation containing all generated artifacts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedProject {

    private String projectId;
    private String projectName;
    private String basePackage;
    private Map<String, String> generatedFiles; // filepath -> content
    private String generatedAt;
    private GenerationStatistics statistics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerationStatistics {
        private int entityCount;
        private int repositoryCount;
        private int controllerCount;
        private int totalFiles;
        private long generationTimeMs;
    }
}
