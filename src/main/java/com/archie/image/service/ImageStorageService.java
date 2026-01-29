package com.archie.image.service;

import com.archie.config.ArchieConfig;
import com.archie.image.model.DiagramImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service for handling diagram image uploads and storage
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final ArchieConfig config;

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/bmp",
            "image/webp");

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Store uploaded diagram image
     */
    public DiagramImage storeImage(MultipartFile file) {
        try {
            // Validate file
            validateFile(file);

            // Generate unique ID
            String imageId = UUID.randomUUID().toString();

            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(config.getUpload().getDirectory());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", uploadPath);
            }

            // Generate stored filename
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String storedFileName = imageId + extension;

            // Save file to disk
            Path filePath = uploadPath.resolve(storedFileName);
            Files.write(filePath, file.getBytes());
            log.info("Stored image file: {}", filePath);

            // Build result
            return DiagramImage.builder()
                    .id(imageId)
                    .originalFileName(originalFilename)
                    .storedFileName(storedFileName)
                    .mimeType(file.getContentType())
                    .fileSize(file.getSize())
                    .uploadedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                    .data(file.getBytes())
                    .build();

        } catch (IOException e) {
            log.error("Error storing image: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store image: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieve stored image by ID
     */
    public DiagramImage getImage(String imageId) {
        try {
            Path uploadPath = Paths.get(config.getUpload().getDirectory());

            // Find file matching the image ID
            Path imagePath = Files.list(uploadPath)
                    .filter(path -> path.getFileName().toString().startsWith(imageId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Image not found: " + imageId));

            // Read file data
            byte[] data = Files.readAllBytes(imagePath);
            String fileName = imagePath.getFileName().toString();

            return DiagramImage.builder()
                    .id(imageId)
                    .storedFileName(fileName)
                    .data(data)
                    .fileSize(data.length)
                    .build();

        } catch (IOException e) {
            log.error("Error retrieving image: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve image: " + e.getMessage(), e);
        }
    }

    /**
     * Delete stored image
     */
    public void deleteImage(String imageId) {
        try {
            Path uploadPath = Paths.get(config.getUpload().getDirectory());

            Files.list(uploadPath)
                    .filter(path -> path.getFileName().toString().startsWith(imageId))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            log.info("Deleted image file: {}", path);
                        } catch (IOException e) {
                            log.error("Error deleting image: {}", e.getMessage());
                        }
                    });

        } catch (IOException e) {
            log.error("Error deleting image: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete image: " + e.getMessage(), e);
        }
    }

    /**
     * Validate uploaded file
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    String.format("File size exceeds maximum allowed size of %d MB", MAX_FILE_SIZE / (1024 * 1024)));
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Invalid file type. Allowed types: " + String.join(", ", ALLOWED_MIME_TYPES));
        }
    }

    /**
     * Extract file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg"; // default
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
