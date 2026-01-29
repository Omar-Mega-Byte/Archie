package com.archie.codegen.generator;

import com.archie.ai.model.DiagramAnalysisResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.StringJoiner;

/**
 * Generates SQL DDL schema from entity metadata
 */
@Slf4j
@Component
public class SchemaGenerator {

    /**
     * Generate SQL schema file
     */
    public String generateSchema(DiagramAnalysisResult analysisResult) {
        log.info("Generating SQL schema for {} entities", analysisResult.getEntities().size());

        StringBuilder schema = new StringBuilder();
        schema.append("-- Auto-generated SQL Schema by Archie - Blueprint to Boot\n");
        schema.append("-- Generated from diagram analysis\n\n");

        // Drop tables in reverse order (for foreign key constraints)
        schema.append("-- Drop existing tables\n");
        for (int i = analysisResult.getEntities().size() - 1; i >= 0; i--) {
            DiagramAnalysisResult.EntityMetadata entity = analysisResult.getEntities().get(i);
            schema.append(String.format("DROP TABLE IF EXISTS %s CASCADE;\n", entity.getTableName()));
        }
        schema.append("\n");

        // Create tables
        for (DiagramAnalysisResult.EntityMetadata entity : analysisResult.getEntities()) {
            schema.append(generateTableDDL(entity));
            schema.append("\n");
        }

        // Add foreign key constraints if relationships exist
        if (analysisResult.getRelationships() != null && !analysisResult.getRelationships().isEmpty()) {
            schema.append("-- Foreign Key Constraints\n");
            for (DiagramAnalysisResult.RelationshipMetadata rel : analysisResult.getRelationships()) {
                schema.append(generateForeignKeyConstraint(rel));
            }
        }

        return schema.toString();
    }

    private String generateTableDDL(DiagramAnalysisResult.EntityMetadata entity) {
        StringBuilder ddl = new StringBuilder();
        ddl.append(String.format("-- Table: %s\n", entity.getName()));
        ddl.append(String.format("CREATE TABLE %s (\n", entity.getTableName()));

        StringJoiner columns = new StringJoiner(",\n    ", "    ", "\n");

        for (DiagramAnalysisResult.AttributeMetadata attr : entity.getAttributes()) {
            columns.add(generateColumnDDL(attr));
        }

        ddl.append(columns.toString());
        ddl.append(");\n");

        // Add comments
        if (!entity.getAttributes().isEmpty()) {
            ddl.append(String.format("\nCOMMENT ON TABLE %s IS '%s entity table';\n",
                    entity.getTableName(), entity.getName()));
        }

        return ddl.toString();
    }

    private String generateColumnDDL(DiagramAnalysisResult.AttributeMetadata attr) {
        StringBuilder column = new StringBuilder();
        column.append(toSnakeCase(attr.getName()));
        column.append(" ");
        column.append(getSQLType(attr));

        if (attr.isPrimaryKey()) {
            column.append(" PRIMARY KEY");
        }

        if (!attr.isNullable()) {
            column.append(" NOT NULL");
        }

        if (attr.isUnique() && !attr.isPrimaryKey()) {
            column.append(" UNIQUE");
        }

        if (attr.getDefaultValue() != null) {
            column.append(" DEFAULT ").append(attr.getDefaultValue());
        }

        return column.toString();
    }

    private String getSQLType(DiagramAnalysisResult.AttributeMetadata attr) {
        String type = attr.getType().toLowerCase();

        return switch (type) {
            case "long", "bigint" -> "BIGINT";
            case "integer", "int" -> "INTEGER";
            case "string", "varchar" -> {
                int length = attr.getLength() != null ? attr.getLength() : 255;
                yield "VARCHAR(" + length + ")";
            }
            case "text" -> "TEXT";
            case "boolean", "bool" -> "BOOLEAN";
            case "double" -> "DOUBLE PRECISION";
            case "float" -> "REAL";
            case "bigdecimal", "decimal" -> "DECIMAL(19,2)";
            case "date" -> "DATE";
            case "timestamp", "datetime", "localdatetime" -> "TIMESTAMP";
            default -> "VARCHAR(255)";
        };
    }

    private String generateForeignKeyConstraint(DiagramAnalysisResult.RelationshipMetadata rel) {
        // Simplified foreign key generation
        String sourceTable = toSnakeCase(rel.getSourceEntity());
        String targetTable = toSnakeCase(rel.getTargetEntity());

        return String.format(
                "-- ALTER TABLE %s ADD CONSTRAINT fk_%s_%s FOREIGN KEY (%s_id) REFERENCES %s(id);\n",
                sourceTable,
                sourceTable,
                targetTable,
                targetTable,
                targetTable);
    }

    private String toSnakeCase(String camelCase) {
        return camelCase
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }
}
