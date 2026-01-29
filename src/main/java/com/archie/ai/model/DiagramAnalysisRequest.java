package com.archie.ai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request model for diagram analysis
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagramAnalysisRequest {

    private byte[] imageData;
    private String imageFileName;
    private String imageMimeType;
    private String additionalInstructions;
}
