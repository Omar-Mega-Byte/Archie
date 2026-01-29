package com.archie.ai.prompt;

import org.springframework.stereotype.Component;

/**
 * Builds prompts for Gemini AI to analyze various diagram types
 */
@Component
public class GeminiPromptBuilder {

  /**
   * Build prompt that auto-detects diagram type and extracts appropriate
   * information
   */
  public String buildDiagramAnalysisPrompt() {
    return """
        You are an expert software architect analyzing a diagram image.

        **STEP 1: Identify the diagram type:**
        - ER Diagram / Database Schema: Boxes with attributes, relationships lines
        - Class Diagram: Classes with methods, inheritance arrows
        - Flowchart: Start/End ovals, process rectangles, decision diamonds
        - Sequence Diagram: Vertical lifelines with horizontal arrows
        - Use Case Diagram: Stick figures, ovals, system boundary
        - Component/Architecture Diagram: Components with interfaces

        **STEP 2: Extract information based on diagram type**

        For **ER Diagrams / Database Schemas**:
        - Extract entities, attributes, types, primary keys
        - Identify relationships (1:1, 1:N, M:N)
        - Map to Java types (VARCHAR→String, INT→Long, etc.)

        For **Class Diagrams**:
        - Extract classes with attributes and methods
        - Identify inheritance, interfaces, associations
        - Note visibility modifiers (+public, -private, #protected)

        For **Flowcharts**:
        - Extract the process flow as a service method
        - Identify conditions, loops, branches
        - Convert to algorithm/pseudocode structure

        For **Sequence Diagrams**:
        - Extract actors/components
        - Identify method calls and responses
        - Generate service interfaces

        **Output Format (strict JSON):**
        ```json
        {
          "diagramType": "ER_DIAGRAM|CLASS_DIAGRAM|FLOWCHART|SEQUENCE_DIAGRAM|USE_CASE|COMPONENT",
          "projectName": "ExtractedProjectName",
          "basePackage": "com.generated",
          "entities": [
            {
              "name": "EntityName",
              "tableName": "entity_names",
              "description": "Brief description of what this entity represents",
              "attributes": [
                {
                  "name": "id",
                  "type": "Long",
                  "nullable": false,
                  "primaryKey": true,
                  "unique": false,
                  "length": null,
                  "defaultValue": null,
                  "description": "Primary identifier"
                }
              ],
              "methods": [
                {
                  "name": "calculateTotal",
                  "returnType": "BigDecimal",
                  "parameters": ["quantity: Integer", "price: BigDecimal"],
                  "description": "Calculate total amount",
                  "visibility": "public"
                }
              ],
              "constraints": []
            }
          ],
          "relationships": [
            {
              "sourceEntity": "Order",
              "targetEntity": "OrderItem",
              "type": "ONE_TO_MANY",
              "mappedBy": "order",
              "bidirectional": true,
              "description": "An order contains multiple items"
            }
          ],
          "services": [
            {
              "name": "PaymentService",
              "description": "Handles payment processing",
              "methods": [
                {
                  "name": "processPayment",
                  "returnType": "PaymentResult",
                  "parameters": ["amount: BigDecimal", "method: PaymentMethod"],
                  "description": "Process a payment transaction",
                  "algorithm": "1. Validate amount\\n2. Connect to payment gateway\\n3. Process transaction\\n4. Return result"
                }
              ]
            }
          ],
          "flowchartLogic": {
            "serviceName": "CounterService",
            "methodName": "executeLoop",
            "description": "Implements the flowchart logic",
            "steps": [
              {"type": "START", "description": "Begin process"},
              {"type": "PROCESS", "description": "Set counter to 0", "code": "int counter = 0;"},
              {"type": "DECISION", "condition": "counter == 7", "trueNext": 4, "falseNext": 3},
              {"type": "PROCESS", "description": "Add 1 to counter", "code": "counter++;"},
              {"type": "PROCESS", "description": "Print School", "code": "System.out.println(\\"School\\");"},
              {"type": "END", "description": "Process complete"}
            ]
          }
        }
        ```

        **Important Rules:**
        - Return ONLY valid JSON, no markdown code blocks
        - Detect diagram type FIRST, then extract relevant information
        - For flowcharts, convert visual logic to executable Java code
        - For class diagrams, include methods with their logic
        - Use standard JPA types: ONE_TO_ONE, ONE_TO_MANY, MANY_TO_ONE, MANY_TO_MANY
        - Entity names should be singular PascalCase
        - Attribute names should be camelCase
        - If diagram is unclear, make reasonable inferences
        - ALWAYS include at least one entity, even if extracted from flowchart context

        Now analyze the provided diagram image and return the JSON structure.
        """;
  }

  public String buildPromptWithContext(String additionalInstructions) {
    String basePrompt = buildDiagramAnalysisPrompt();

    if (additionalInstructions != null && !additionalInstructions.isBlank()) {
      return basePrompt + "\n\n**Additional Context:**\n" + additionalInstructions;
    }

    return basePrompt;
  }
}
