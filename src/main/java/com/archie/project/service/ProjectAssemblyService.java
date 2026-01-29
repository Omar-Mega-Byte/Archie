package com.archie.project.service;

import com.archie.codegen.model.GeneratedProject;
import com.archie.config.ArchieConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Service for assembling and packaging generated projects
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectAssemblyService {

    private final ArchieConfig config;

    /**
     * Create ZIP file containing the complete Spring Boot project
     */
    public byte[] createProjectZip(GeneratedProject project) {
        try {
            log.info("Creating ZIP package for project: {}", project.getProjectName());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                String projectDir = sanitizeProjectName(project.getProjectName()) + "/";

                // Add all generated files to ZIP
                for (Map.Entry<String, String> file : project.getGeneratedFiles().entrySet()) {
                    String filePath = projectDir + file.getKey();
                    String content = file.getValue();

                    ZipEntry entry = new ZipEntry(filePath);
                    zos.putNextEntry(entry);
                    zos.write(content.getBytes(StandardCharsets.UTF_8));
                    zos.closeEntry();
                }

                // Add .gitignore
                String gitignore = generateGitignore();
                ZipEntry gitignoreEntry = new ZipEntry(projectDir + ".gitignore");
                zos.putNextEntry(gitignoreEntry);
                zos.write(gitignore.getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }

            byte[] zipBytes = baos.toByteArray();
            log.info("Created ZIP package: {} bytes", zipBytes.length);

            return zipBytes;

        } catch (IOException e) {
            log.error("Error creating project ZIP: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create project ZIP: " + e.getMessage(), e);
        }
    }

    /**
     * Save generated project to disk
     */
    public Path saveProjectToDisk(GeneratedProject project) {
        try {
            String projectDirName = sanitizeProjectName(project.getProjectName());
            Path projectPath = Paths.get(config.getGeneration().getOutputDirectory(), projectDirName);

            log.info("Saving project to disk: {}", projectPath);

            // Create project directory
            Files.createDirectories(projectPath);

            // Write all files
            for (Map.Entry<String, String> file : project.getGeneratedFiles().entrySet()) {
                Path filePath = projectPath.resolve(file.getKey());
                Files.createDirectories(filePath.getParent());
                Files.writeString(filePath, file.getValue(), StandardCharsets.UTF_8);
            }

            // Write .gitignore
            Path gitignorePath = projectPath.resolve(".gitignore");
            Files.writeString(gitignorePath, generateGitignore(), StandardCharsets.UTF_8);

            log.info("Project saved successfully to: {}", projectPath);
            return projectPath;

        } catch (IOException e) {
            log.error("Error saving project to disk: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save project: " + e.getMessage(), e);
        }
    }

    private String sanitizeProjectName(String projectName) {
        return projectName
                .toLowerCase()
                .replaceAll("[^a-z0-9-]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    private String generateGitignore() {
        return """
                # Maven
                target/
                pom.xml.tag
                pom.xml.releaseBackup
                pom.xml.versionsBackup
                pom.xml.next
                release.properties

                # IDE
                .idea/
                *.iml
                .vscode/
                .classpath
                .project
                .settings/

                # OS
                .DS_Store
                Thumbs.db

                # Logs
                *.log

                # Application
                application-local.yml
                """;
    }
}
