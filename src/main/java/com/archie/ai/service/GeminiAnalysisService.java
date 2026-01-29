package com.archie.ai.service;

import com.archie.ai.model.DiagramAnalysisRequest;
import com.archie.ai.model.DiagramAnalysisResult;
import com.archie.ai.prompt.GeminiPromptBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Service for analyzing architectural diagrams using Google Gemini AI
 * Uses Google AI Studio API (FREE - no billing required)
 */
@Slf4j
@Service
public class GeminiAnalysisService {

    private final GeminiPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${gemini.api.key:${GEMINI_API_KEY:}}")
    private String apiKey;

    @Value("${gemini.model:${GEMINI_MODEL:gemini-2.0-flash}}")
    private String model;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    public GeminiAnalysisService(GeminiPromptBuilder promptBuilder, ObjectMapper objectMapper) {
        this.promptBuilder = promptBuilder;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Analyze a diagram image and extract entity/relationship metadata
     * Uses Gemini's vision capabilities for image understanding
     */
    public DiagramAnalysisResult analyzeDiagram(DiagramAnalysisRequest request) {
        try {
            log.info("Starting diagram analysis for file: {}", request.getImageFileName());

            // Build prompt
            String promptText = promptBuilder.buildPromptWithContext(request.getAdditionalInstructions());

            // Full prompt for Gemini vision analysis
            String fullPrompt = "Analyze the uploaded architectural diagram image and extract all entities, their attributes, and relationships.\n\n"
                    +
                    promptText;

            // Call Gemini API
            log.debug("Sending request to Gemini API (model: {})...", model);
            String response = callGeminiApi(fullPrompt, request.getImageData(), request.getImageMimeType());

            log.debug("Received response from Gemini: {}", response);

            // Parse JSON response
            DiagramAnalysisResult result = parseAIResponse(response);
            result.setRawResponse(response);

            log.info("Successfully analyzed diagram. Found {} entities and {} relationships",
                    result.getEntities() != null ? result.getEntities().size() : 0,
                    result.getRelationships() != null ? result.getRelationships().size() : 0);

            return result;

        } catch (Exception e) {
            log.error("Error analyzing diagram: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to analyze diagram: " + e.getMessage(), e);
        }
    }

    /**
     * Call Gemini API with text and optional image
     */
    private String callGeminiApi(String prompt, byte[] imageData, String mimeType) {
        String url = String.format(GEMINI_API_URL, model, apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build request body
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> content = new HashMap<>();
        List<Map<String, Object>> parts = new ArrayList<>();

        // Add image part if provided
        if (imageData != null && imageData.length > 0) {
            Map<String, Object> imagePart = new HashMap<>();
            Map<String, Object> inlineData = new HashMap<>();
            inlineData.put("mimeType", mimeType != null ? mimeType : "image/png");
            inlineData.put("data", Base64.getEncoder().encodeToString(imageData));
            imagePart.put("inlineData", inlineData);
            parts.add(imagePart);
        }

        // Add text part
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);
        parts.add(textPart);

        content.put("parts", parts);
        contents.add(content);
        requestBody.put("contents", contents);

        // Add generation config
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.3);
        generationConfig.put("maxOutputTokens", 8192);
        requestBody.put("generationConfig", generationConfig);

        try {
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            // Parse response to extract text
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode contentNode = firstCandidate.path("content");
                JsonNode partsNode = contentNode.path("parts");
                if (partsNode.isArray() && partsNode.size() > 0) {
                    return partsNode.get(0).path("text").asText();
                }
            }

            throw new RuntimeException("Invalid response from Gemini API: " + response.getBody());

        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage());
            throw new RuntimeException("Gemini API call failed: " + e.getMessage(), e);
        }
    }

    /**
     * Parse AI's JSON response into structured result
     */
    private DiagramAnalysisResult parseAIResponse(String response) {
        try {
            // Remove markdown code blocks if present
            String cleanedResponse = response
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            // Parse JSON
            DiagramAnalysisResult result = objectMapper.readValue(cleanedResponse, DiagramAnalysisResult.class);

            // Validate result
            if (result.getEntities() == null || result.getEntities().isEmpty()) {
                throw new IllegalArgumentException("No entities found in diagram analysis");
            }

            return result;

        } catch (Exception e) {
            log.error("Error parsing Gemini response: {}", e.getMessage());
            log.debug("Raw response: {}", response);
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage(), e);
        }
    }
}
