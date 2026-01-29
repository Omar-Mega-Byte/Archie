package com.archie.image.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents uploaded diagram image metadata
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagramImage {

    private String id;
    private String originalFileName;
    private String storedFileName;
    private String mimeType;
    private long fileSize;
    private String uploadedAt;
    private byte[] data;
}
