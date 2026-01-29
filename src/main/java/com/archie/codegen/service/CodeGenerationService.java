package com.archie.codegen.service;

import com.archie.ai.model.DiagramAnalysisResult;
import com.archie.codegen.generator.ControllerGenerator;
import com.archie.codegen.generator.EntityGenerator;
import com.archie.codegen.generator.RepositoryGenerator;
import com.archie.codegen.generator.SchemaGenerator;
import com.archie.codegen.model.GeneratedProject;
import com.archie.config.DatabaseType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Orchestrates code generation from diagram analysis
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CodeGenerationService {

    private final EntityGenerator entityGenerator;
    private final RepositoryGenerator repositoryGenerator;
    private final ControllerGenerator controllerGenerator;
    private final SchemaGenerator schemaGenerator;

    /**
     * Generate complete Spring Boot project from diagram analysis (default H2)
     */
    public GeneratedProject generateProject(DiagramAnalysisResult analysisResult) {
        return generateProject(analysisResult, DatabaseType.H2);
    }

    /**
     * Generate complete Spring Boot project with specified database
     */
    public GeneratedProject generateProject(DiagramAnalysisResult analysisResult, DatabaseType databaseType) {
        long startTime = System.currentTimeMillis();

        try {
            log.info("Starting code generation for project: {} with database: {}",
                    analysisResult.getProjectName(), databaseType);

            Map<String, String> allFiles = new LinkedHashMap<>();

            // Generate entities
            log.info("Generating entities...");
            Map<String, String> entities = entityGenerator.generateAllEntities(analysisResult);
            entities.forEach((fileName, content) -> allFiles
                    .put("src/main/java/" + toPath(analysisResult.getBasePackage()) + "/entity/" + fileName, content));

            // Generate repositories
            log.info("Generating repositories...");
            Map<String, String> repositories = repositoryGenerator.generateAllRepositories(analysisResult);
            repositories.forEach((fileName, content) -> allFiles.put(
                    "src/main/java/" + toPath(analysisResult.getBasePackage()) + "/repository/" + fileName, content));

            // Generate controllers
            log.info("Generating controllers...");
            Map<String, String> controllers = controllerGenerator.generateAllControllers(analysisResult);
            controllers.forEach((fileName, content) -> allFiles.put(
                    "src/main/java/" + toPath(analysisResult.getBasePackage()) + "/controller/" + fileName, content));

            // Generate services from flowchart/class diagram
            if (analysisResult.getServices() != null && !analysisResult.getServices().isEmpty()) {
                log.info("Generating services...");
                Map<String, String> services = generateServices(analysisResult);
                services.forEach((fileName, content) -> allFiles.put(
                        "src/main/java/" + toPath(analysisResult.getBasePackage()) + "/service/" + fileName, content));
            }

            // Generate flowchart service if present
            if (analysisResult.getFlowchartLogic() != null) {
                log.info("Generating flowchart service...");
                String flowchartService = generateFlowchartService(analysisResult);
                allFiles.put("src/main/java/" + toPath(analysisResult.getBasePackage()) + "/service/" +
                        analysisResult.getFlowchartLogic().getServiceName() + ".java", flowchartService);
            }

            // Generate SQL schema
            log.info("Generating SQL schema...");
            String schema = schemaGenerator.generateSchema(analysisResult);
            allFiles.put("src/main/resources/schema.sql", schema);

            // Generate application.yml with selected database
            String applicationYml = generateApplicationYml(analysisResult, databaseType);
            allFiles.put("src/main/resources/application.yml", applicationYml);

            // Generate pom.xml with database dependency
            String pomXml = generatePomXml(analysisResult, databaseType);
            allFiles.put("pom.xml", pomXml);

            // Generate main application class
            String mainApp = generateMainApplication(analysisResult);
            allFiles.put("src/main/java/" + toPath(analysisResult.getBasePackage()) + "/Application.java", mainApp);

            // Generate docker-compose if not H2
            if (databaseType != DatabaseType.H2) {
                String dockerCompose = generateDockerCompose(analysisResult, databaseType);
                allFiles.put("docker-compose.yml", dockerCompose);
            }

            // Generate README
            String readme = generateReadme(analysisResult, databaseType);
            allFiles.put("README.md", readme);

            long generationTime = System.currentTimeMillis() - startTime;

            // Build statistics
            GeneratedProject.GenerationStatistics stats = GeneratedProject.GenerationStatistics.builder()
                    .entityCount(entities.size())
                    .repositoryCount(repositories.size())
                    .controllerCount(controllers.size())
                    .totalFiles(allFiles.size())
                    .generationTimeMs(generationTime)
                    .build();

            log.info("Code generation completed in {}ms. Generated {} files", generationTime, allFiles.size());

            return GeneratedProject.builder()
                    .projectId(UUID.randomUUID().toString())
                    .projectName(analysisResult.getProjectName())
                    .basePackage(analysisResult.getBasePackage())
                    .generatedFiles(allFiles)
                    .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                    .statistics(stats)
                    .build();

        } catch (Exception e) {
            log.error("Error during code generation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate project: " + e.getMessage(), e);
        }
    }

    private Map<String, String> generateServices(DiagramAnalysisResult analysisResult) {
        Map<String, String> services = new LinkedHashMap<>();

        for (DiagramAnalysisResult.ServiceMetadata service : analysisResult.getServices()) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("package %s.service;\n\n", analysisResult.getBasePackage()));
            sb.append("import org.springframework.stereotype.Service;\n");
            sb.append("import lombok.RequiredArgsConstructor;\n");
            sb.append("import lombok.extern.slf4j.Slf4j;\n\n");
            sb.append("/**\n * ").append(service.getDescription()).append("\n */\n");
            sb.append("@Slf4j\n@Service\n@RequiredArgsConstructor\n");
            sb.append("public class ").append(service.getName()).append(" {\n\n");

            if (service.getMethods() != null) {
                for (DiagramAnalysisResult.MethodMetadata method : service.getMethods()) {
                    sb.append("    /**\n     * ").append(method.getDescription()).append("\n     */\n");
                    sb.append("    public ").append(method.getReturnType() != null ? method.getReturnType() : "void");
                    sb.append(" ").append(method.getName()).append("(");
                    if (method.getParameters() != null) {
                        sb.append(String.join(", ", method.getParameters()));
                    }
                    sb.append(") {\n");
                    if (method.getAlgorithm() != null) {
                        sb.append("        // Algorithm:\n");
                        for (String step : method.getAlgorithm().split("\\\\n")) {
                            sb.append("        // ").append(step).append("\n");
                        }
                    }
                    sb.append("        // TODO: Implement ").append(method.getName()).append("\n");
                    if (method.getReturnType() != null && !"void".equals(method.getReturnType())) {
                        sb.append("        return null;\n");
                    }
                    sb.append("    }\n\n");
                }
            }

            sb.append("}\n");
            services.put(service.getName() + ".java", sb.toString());
        }

        return services;
    }

    private String generateFlowchartService(DiagramAnalysisResult analysisResult) {
        DiagramAnalysisResult.FlowchartLogic logic = analysisResult.getFlowchartLogic();
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("package %s.service;\n\n", analysisResult.getBasePackage()));
        sb.append("import org.springframework.stereotype.Service;\n");
        sb.append("import lombok.extern.slf4j.Slf4j;\n\n");
        sb.append("/**\n * ").append(logic.getDescription()).append("\n");
        sb.append(" * Generated from flowchart diagram\n */\n");
        sb.append("@Slf4j\n@Service\n");
        sb.append("public class ").append(logic.getServiceName()).append(" {\n\n");
        sb.append("    /**\n     * Execute the flowchart logic\n     */\n");
        sb.append("    public void ").append(logic.getMethodName()).append("() {\n");
        sb.append("        log.info(\"Starting: ").append(logic.getDescription()).append("\");\n\n");

        // Generate code from steps
        if (logic.getSteps() != null) {
            for (DiagramAnalysisResult.FlowchartStep step : logic.getSteps()) {
                sb.append("        // ").append(step.getDescription()).append("\n");
                if (step.getCode() != null && !step.getCode().isEmpty()) {
                    sb.append("        ").append(step.getCode()).append("\n");
                }
            }
        }

        sb.append("\n        log.info(\"Completed: ").append(logic.getDescription()).append("\");\n");
        sb.append("    }\n}\n");

        return sb.toString();
    }

    private String generateMainApplication(DiagramAnalysisResult analysisResult) {
        return String.format("""
                package %s;

                import org.springframework.boot.SpringApplication;
                import org.springframework.boot.autoconfigure.SpringBootApplication;

                @SpringBootApplication
                public class Application {
                    public static void main(String[] args) {
                        SpringApplication.run(Application.class, args);
                    }
                }
                """, analysisResult.getBasePackage());
    }

    private String toPath(String packageName) {
        return packageName.replace('.', '/');
    }

    private String generateApplicationYml(DiagramAnalysisResult analysisResult, DatabaseType dbType) {
        String projectName = analysisResult.getProjectName().toLowerCase().replace(" ", "-");

        return switch (dbType) {
            case H2 -> String.format("""
                    spring:
                      application:
                        name: %s

                      datasource:
                        url: jdbc:h2:mem:testdb
                        driver-class-name: org.h2.Driver
                        username: sa
                        password:

                      jpa:
                        hibernate:
                          ddl-auto: update
                        show-sql: true
                        properties:
                          hibernate:
                            format_sql: true

                      h2:
                        console:
                          enabled: true
                          path: /h2-console

                    server:
                      port: 8080

                    logging:
                      level:
                        root: INFO
                        %s: DEBUG
                    """, projectName, analysisResult.getBasePackage());

            case POSTGRESQL -> String.format("""
                    spring:
                      application:
                        name: %s

                      datasource:
                        url: jdbc:postgresql://localhost:5432/%s
                        driver-class-name: org.postgresql.Driver
                        username: postgres
                        password: postgres

                      jpa:
                        hibernate:
                          ddl-auto: update
                        show-sql: true
                        properties:
                          hibernate:
                            dialect: org.hibernate.dialect.PostgreSQLDialect
                            format_sql: true

                    server:
                      port: 8080

                    logging:
                      level:
                        root: INFO
                        %s: DEBUG
                    """, projectName, projectName, analysisResult.getBasePackage());

            case MYSQL -> String.format("""
                    spring:
                      application:
                        name: %s

                      datasource:
                        url: jdbc:mysql://localhost:3306/%s?useSSL=false&serverTimezone=UTC
                        driver-class-name: com.mysql.cj.jdbc.Driver
                        username: root
                        password: root

                      jpa:
                        hibernate:
                          ddl-auto: update
                        show-sql: true
                        properties:
                          hibernate:
                            dialect: org.hibernate.dialect.MySQLDialect
                            format_sql: true

                    server:
                      port: 8080

                    logging:
                      level:
                        root: INFO
                        %s: DEBUG
                    """, projectName, projectName, analysisResult.getBasePackage());

            case MONGODB -> String.format("""
                    spring:
                      application:
                        name: %s

                      data:
                        mongodb:
                          uri: mongodb://localhost:27017/%s

                    server:
                      port: 8080

                    logging:
                      level:
                        root: INFO
                        %s: DEBUG
                    """, projectName, projectName, analysisResult.getBasePackage());

            case SQLITE -> String.format("""
                    spring:
                      application:
                        name: %s

                      datasource:
                        url: jdbc:sqlite:%s.db
                        driver-class-name: org.sqlite.JDBC

                      jpa:
                        hibernate:
                          ddl-auto: update
                        show-sql: true
                        properties:
                          hibernate:
                            dialect: org.hibernate.community.dialect.SQLiteDialect
                            format_sql: true

                    server:
                      port: 8080

                    logging:
                      level:
                        root: INFO
                        %s: DEBUG
                    """, projectName, projectName, analysisResult.getBasePackage());
        };
    }

    private String generateDockerCompose(DiagramAnalysisResult analysisResult, DatabaseType dbType) {
        String projectName = analysisResult.getProjectName().toLowerCase().replace(" ", "-");

        return switch (dbType) {
            case POSTGRESQL -> String.format("""
                    version: '3.8'
                    services:
                      postgres:
                        image: postgres:15
                        container_name: %s-postgres
                        environment:
                          POSTGRES_DB: %s
                          POSTGRES_USER: postgres
                          POSTGRES_PASSWORD: postgres
                        ports:
                          - "5432:5432"
                        volumes:
                          - postgres_data:/var/lib/postgresql/data

                    volumes:
                      postgres_data:
                    """, projectName, projectName);

            case MYSQL -> String.format("""
                    version: '3.8'
                    services:
                      mysql:
                        image: mysql:8
                        container_name: %s-mysql
                        environment:
                          MYSQL_ROOT_PASSWORD: root
                          MYSQL_DATABASE: %s
                        ports:
                          - "3306:3306"
                        volumes:
                          - mysql_data:/var/lib/mysql

                    volumes:
                      mysql_data:
                    """, projectName, projectName);

            case MONGODB -> String.format("""
                    version: '3.8'
                    services:
                      mongodb:
                        image: mongo:6
                        container_name: %s-mongodb
                        ports:
                          - "27017:27017"
                        volumes:
                          - mongo_data:/data/db

                    volumes:
                      mongo_data:
                    """, projectName);

            default -> "";
        };
    }

    private String generatePomXml(DiagramAnalysisResult analysisResult, DatabaseType dbType) {
        String artifactId = analysisResult.getProjectName().toLowerCase().replace(" ", "-");

        String dbDependency = switch (dbType) {
            case H2 -> """
                    <dependency>
                        <groupId>com.h2database</groupId>
                        <artifactId>h2</artifactId>
                        <scope>runtime</scope>
                    </dependency>""";
            case POSTGRESQL -> """
                    <dependency>
                        <groupId>org.postgresql</groupId>
                        <artifactId>postgresql</artifactId>
                        <scope>runtime</scope>
                    </dependency>""";
            case MYSQL -> """
                    <dependency>
                        <groupId>com.mysql</groupId>
                        <artifactId>mysql-connector-j</artifactId>
                        <scope>runtime</scope>
                    </dependency>""";
            case MONGODB -> """
                    <dependency>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-data-mongodb</artifactId>
                    </dependency>""";
            case SQLITE -> """
                    <dependency>
                        <groupId>org.xerial</groupId>
                        <artifactId>sqlite-jdbc</artifactId>
                        <version>3.44.1.0</version>
                    </dependency>
                    <dependency>
                        <groupId>org.hibernate.orm</groupId>
                        <artifactId>hibernate-community-dialects</artifactId>
                    </dependency>""";
        };

        String jpaStarter = dbType == DatabaseType.MONGODB ? "" : """
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-data-jpa</artifactId>
                </dependency>""";

        return String.format("""
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                         https://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>

                    <parent>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-parent</artifactId>
                        <version>3.3.6</version>
                    </parent>

                    <groupId>%s</groupId>
                    <artifactId>%s</artifactId>
                    <version>1.0.0-SNAPSHOT</version>
                    <name>%s</name>
                    <description>Generated by Archie - Blueprint to Boot</description>

                    <properties>
                        <java.version>21</java.version>
                    </properties>

                    <dependencies>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-web</artifactId>
                        </dependency>
                %s
                %s
                        <dependency>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <optional>true</optional>
                        </dependency>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-validation</artifactId>
                        </dependency>
                        <dependency>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-test</artifactId>
                            <scope>test</scope>
                        </dependency>
                    </dependencies>

                    <build>
                        <plugins>
                            <plugin>
                                <groupId>org.springframework.boot</groupId>
                                <artifactId>spring-boot-maven-plugin</artifactId>
                            </plugin>
                        </plugins>
                    </build>
                </project>
                """,
                analysisResult.getBasePackage(),
                artifactId,
                analysisResult.getProjectName(),
                jpaStarter,
                dbDependency);
    }

    private String generateReadme(DiagramAnalysisResult analysisResult, DatabaseType dbType) {
        String dbSetup = switch (dbType) {
            case H2 -> "H2 Console: http://localhost:8080/h2-console";
            case POSTGRESQL -> """
                    ## Database Setup
                    ```bash
                    # Start PostgreSQL with Docker
                    docker-compose up -d
                    ```""";
            case MYSQL -> """
                    ## Database Setup
                    ```bash
                    # Start MySQL with Docker
                    docker-compose up -d
                    ```""";
            case MONGODB -> """
                    ## Database Setup
                    ```bash
                    # Start MongoDB with Docker
                    docker-compose up -d
                    ```""";
            case SQLITE ->
                "SQLite database file: ./" + analysisResult.getProjectName().toLowerCase().replace(" ", "-") + ".db";
        };

        return String.format("""
                # %s

                > Generated by **Archie - Blueprint to Boot** 🤖

                ## Project Overview

                This Spring Boot project was auto-generated from a diagram.

                - **Diagram Type:** %s
                - **Database:** %s
                - **Entities:** %d
                - **Generated:** %s

                ## Quick Start

                ```bash
                # Build the project
                mvn clean install

                # Run the application
                mvn spring-boot:run
                ```

                %s

                ## API Endpoints

                - **Application:** http://localhost:8080
                - **API Base:** `/api/{entity-name}s`

                ## Features

                ✅ JPA Entities with relationships
                ✅ Spring Data Repositories
                ✅ REST Controllers with CRUD operations
                ✅ SQL Schema initialization
                ✅ %s Database

                ## Technology Stack

                - Java 21
                - Spring Boot 3.3
                - Spring Data JPA
                - %s
                - Lombok

                ---

                Generated with ❤️ by Archie
                """,
                analysisResult.getProjectName(),
                analysisResult.getDiagramType() != null ? analysisResult.getDiagramType() : "ER_DIAGRAM",
                dbType.getDisplayName(),
                analysisResult.getEntities() != null ? analysisResult.getEntities().size() : 0,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                dbSetup,
                dbType.getDisplayName(),
                dbType.getDisplayName());
    }
}
