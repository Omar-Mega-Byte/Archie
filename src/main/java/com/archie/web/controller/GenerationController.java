package com.archie.web.controller;

import com.archie.ai.model.DiagramAnalysisRequest;
import com.archie.ai.model.DiagramAnalysisResult;
import com.archie.ai.service.GeminiAnalysisService;
import com.archie.auth.entity.User;
import com.archie.auth.repository.UserRepository;
import com.archie.codegen.model.GeneratedProject;
import com.archie.codegen.service.CodeGenerationService;
import com.archie.config.DatabaseType;
import com.archie.image.model.DiagramImage;
import com.archie.image.service.ImageStorageService;
import com.archie.project.service.ProjectAssemblyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * REST API controller for diagram processing and code generation
 */
@Slf4j
@RestController
@RequestMapping("/api/generate")
@RequiredArgsConstructor
@Tag(name = "Code Generation", description = "APIs for diagram analysis and code generation")
public class GenerationController {

    private final ImageStorageService imageStorageService;
    private final GeminiAnalysisService geminiAnalysisService;
    private final CodeGenerationService codeGenerationService;
    private final ProjectAssemblyService projectAssemblyService;
    private final UserRepository userRepository;

    // In-memory storage for generated projects (for demo purposes)
    private final Map<String, GeneratedProject> projectCache = new ConcurrentHashMap<>();

    @GetMapping("/database-types")
    @Operation(summary = "Get available database types", description = "Returns list of supported databases")
    public ResponseEntity<java.util.List<DatabaseOption>> getDatabaseTypes() {
        var options = Arrays.stream(DatabaseType.values())
                .map(db -> new DatabaseOption(db.name(), db.getDisplayName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(options);
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Analyze diagram and generate code", description = "Upload a hand-drawn diagram to generate Spring Boot project")
    @Transactional
    public ResponseEntity<GenerationResponse> analyzeAndGenerate(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "instructions", required = false) String additionalInstructions,
            @RequestParam(value = "database", required = false, defaultValue = "H2") String databaseType) {

        long startTime = System.currentTimeMillis();

        try {
            log.info("Received generation request for file: {}, database: {}", file.getOriginalFilename(),
                    databaseType);

            // Parse database type
            DatabaseType dbType;
            try {
                dbType = DatabaseType.valueOf(databaseType.toUpperCase());
            } catch (IllegalArgumentException e) {
                dbType = DatabaseType.H2;
            }

            // Step 1: Store image
            DiagramImage image = imageStorageService.storeImage(file);
            log.info("Image stored with ID: {}", image.getId());

            // Step 2: Analyze with Gemini
            DiagramAnalysisRequest analysisRequest = DiagramAnalysisRequest.builder()
                    .imageData(image.getData())
                    .imageFileName(image.getOriginalFileName())
                    .imageMimeType(image.getMimeType())
                    .additionalInstructions(additionalInstructions)
                    .build();

            DiagramAnalysisResult analysisResult = geminiAnalysisService.analyzeDiagram(analysisRequest);
            log.info("Diagram analyzed: type={}, {} entities, {} relationships",
                    analysisResult.getDiagramType(),
                    analysisResult.getEntities() != null ? analysisResult.getEntities().size() : 0,
                    analysisResult.getRelationships() != null ? analysisResult.getRelationships().size() : 0);

            // Step 3: Generate code with selected database
            GeneratedProject project = codeGenerationService.generateProject(analysisResult, dbType);
            log.info("Code generation completed: {} files generated", project.getGeneratedFiles().size());

            // Cache the project for download
            projectCache.put(project.getProjectId(), project);

            // Update user statistics
            long generationTime = System.currentTimeMillis() - startTime;
            int entityCount = project.getStatistics() != null ? project.getStatistics().getEntityCount() : 0;
            updateUserStats(generationTime, entityCount);

            // Build response
            GenerationResponse response = GenerationResponse.builder()
                    .success(true)
                    .message("Project generated successfully")
                    .imageId(image.getId())
                    .projectId(project.getProjectId())
                    .projectName(project.getProjectName())
                    .diagramType(analysisResult.getDiagramType())
                    .databaseType(dbType.name())
                    .analysisResult(analysisResult)
                    .generatedFiles(project.getGeneratedFiles())
                    .statistics(project.getStatistics())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error during generation: {}", e.getMessage(), e);

            GenerationResponse errorResponse = GenerationResponse.builder()
                    .success(false)
                    .message("Generation failed: " + e.getMessage())
                    .build();

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/update-file")
    @Operation(summary = "Update a generated file", description = "Update content of a specific file in the project")
    public ResponseEntity<GenerationResponse> updateFile(
            @RequestBody FileUpdateRequest request) {
        try {
            GeneratedProject project = projectCache.get(request.getProjectId());
            if (project == null) {
                return ResponseEntity.notFound().build();
            }

            // Update the file content
            project.getGeneratedFiles().put(request.getFilePath(), request.getContent());
            log.info("Updated file: {} in project: {}", request.getFilePath(), request.getProjectId());

            GenerationResponse response = GenerationResponse.builder()
                    .success(true)
                    .message("File updated successfully")
                    .projectId(request.getProjectId())
                    .generatedFiles(project.getGeneratedFiles())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error updating file: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    GenerationResponse.builder()
                            .success(false)
                            .message("Update failed: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/download/{projectId}")
    @Operation(summary = "Download generated project as ZIP", description = "Download the complete Spring Boot project")
    public ResponseEntity<byte[]> downloadProject(@PathVariable String projectId) {
        try {
            GeneratedProject project = projectCache.get(projectId);
            if (project == null) {
                log.warn("Project not found: {}", projectId);
                return ResponseEntity.notFound().build();
            }

            // Create ZIP file
            byte[] zipBytes = createZipFile(project);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", project.getProjectName() + ".zip");
            headers.setContentLength(zipBytes.length);

            log.info("Downloading project: {} ({} bytes)", project.getProjectName(), zipBytes.length);
            return ResponseEntity.ok().headers(headers).body(zipBytes);

        } catch (Exception e) {
            log.error("Error downloading project: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private void updateUserStats(long generationTime, int entityCount) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                String username = auth.getName();
                userRepository.findByUsername(username).ifPresent(user -> {
                    userRepository.incrementUserStats(user.getId(), generationTime, entityCount);
                    log.info("Updated stats for user: {} - added {} entities", username, entityCount);
                });
            }
        } catch (Exception e) {
            log.warn("Failed to update user stats: {}", e.getMessage());
        }
    }

    private byte[] createZipFile(GeneratedProject project) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            String rootFolder = project.getProjectName() + "/";

            for (Map.Entry<String, String> entry : project.getGeneratedFiles().entrySet()) {
                String filePath = rootFolder + entry.getKey();
                ZipEntry zipEntry = new ZipEntry(filePath);
                zos.putNextEntry(zipEntry);
                zos.write(entry.getValue().getBytes());
                zos.closeEntry();
            }
        }
        return baos.toByteArray();
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class GenerationResponse {
        private boolean success;
        private String message;
        private String imageId;
        private String projectId;
        private String projectName;
        private String diagramType;
        private String databaseType;
        private DiagramAnalysisResult analysisResult;
        private Map<String, String> generatedFiles;
        private GeneratedProject.GenerationStatistics statistics;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DatabaseOption {
        private String value;
        private String label;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class FileUpdateRequest {
        private String projectId;
        private String filePath;
        private String content;
    }
}
