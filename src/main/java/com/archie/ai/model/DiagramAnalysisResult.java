package com.archie.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Result of diagram analysis containing extracted entities, relationships, and
 * services
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagramAnalysisResult {

    private String diagramType; // ER_DIAGRAM, CLASS_DIAGRAM, FLOWCHART, SEQUENCE_DIAGRAM, etc.
    private String projectName;
    private String basePackage;
    private List<EntityMetadata> entities;
    private List<RelationshipMetadata> relationships;
    private List<ServiceMetadata> services;
    private FlowchartLogic flowchartLogic;
    private String rawResponse;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityMetadata {
        private String name;
        private String tableName;
        private String description;
        private List<AttributeMetadata> attributes;
        private List<MethodMetadata> methods;
        private List<String> constraints;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttributeMetadata {
        private String name;
        private String type;
        private boolean nullable;
        private boolean primaryKey;
        private boolean unique;
        private Integer length;
        private String defaultValue;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MethodMetadata {
        private String name;
        private String returnType;
        private List<String> parameters;
        private String description;
        private String visibility;
        private String algorithm;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelationshipMetadata {
        private String sourceEntity;
        private String targetEntity;
        private RelationType type;
        private String mappedBy;
        private boolean bidirectional;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceMetadata {
        private String name;
        private String description;
        private List<MethodMetadata> methods;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowchartLogic {
        private String serviceName;
        private String methodName;
        private String description;
        private List<FlowchartStep> steps;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowchartStep {
        private String type; // START, END, PROCESS, DECISION
        private String description;
        private String code;
        private String condition;
        private Integer trueNext;
        private Integer falseNext;
    }

    public enum RelationType {
        ONE_TO_ONE,
        ONE_TO_MANY,
        MANY_TO_ONE,
        MANY_TO_MANY
    }
}
