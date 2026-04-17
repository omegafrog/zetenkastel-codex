package com.zetenkastel.core.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetenkastel.core.port.LlmPort;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class OllamaLlmAdapter implements LlmPort {

    private final String baseUrl;
    private final String model;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OllamaLlmAdapter(String baseUrl, String model) {
        this.baseUrl = baseUrl;
        this.model = model;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public LlmResponse analyze(String noteContent, String instruction) {
        String prompt = buildPrompt(noteContent, instruction);
        String jsonBody = buildRequestBody(prompt);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/generate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .timeout(Duration.ofSeconds(180))
                    .build();

            HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return new LlmResponse(LlmResponse.ActionType.KEEP, null, null, 
                        "LLM API error: " + response.statusCode());
            }

            return parseResponse(response.body());
        } catch (Exception e) {
            return new LlmResponse(LlmResponse.ActionType.KEEP, null, null, 
                    "LLM call failed: " + e.getMessage());
        }
    }

    private String buildPrompt(String noteContent, String instruction) {
        String truncatedContent = noteContent.length() > 1000 
            ? noteContent.substring(0, 1000) + "..." 
            : noteContent;
        
        String escapedContent = truncatedContent.replace("%", "%%");
        
        return """
            You are a note triage assistant.
            
            Actions:
            - MOVE: Move to another note type
            - REWRITE: Rewrite with improvements
            - ARCHIVE: Move to archives
            - KEEP: Keep in inbox
            
            REWRITE rules:
            - Keep original language (Korean→Korean, English→English)
            - Korean notes must be 100%% Hangul, no hanja
            - Keep meaning, improve clarity
            
            Note content:
            %s
            
            Respond JSON:
            {"action": "MOVE|REWRITE|ARCHIVE|KEEP", "targetNoteType": "type", "newContent": "rewritten", "reason": "why"}
            """.formatted(escapedContent);
    }

    private String buildRequestBody(String prompt) {
        return """
            {
                "model": "%s",
                "prompt": %s,
                "stream": false,
                "format": "json"
            }
            """.formatted(model, escapeJson(prompt));
    }

    private String escapeJson(String text) {
        return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }

    private LlmResponse parseResponse(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            String responseText = root.path("response").asText();
            JsonNode parsed = objectMapper.readTree(responseText);
            
            String actionStr = parsed.path("action").asText("KEEP");
            LlmResponse.ActionType action;
            try {
                action = LlmResponse.ActionType.valueOf(actionStr);
            } catch (IllegalArgumentException e) {
                action = LlmResponse.ActionType.KEEP;
            }
            String targetNoteType = parsed.path("targetNoteType").asText(null);
            String newContent = parsed.path("newContent").asText(null);
            String reason = parsed.path("reason").asText("");

            if (action == LlmResponse.ActionType.REWRITE && (newContent == null || newContent.isBlank())) {
                return new LlmResponse(LlmResponse.ActionType.KEEP, null, null, 
                        "REWRITE requested but no new content provided");
            }

            return new LlmResponse(action, targetNoteType, newContent, reason);
        } catch (Exception e) {
            return new LlmResponse(LlmResponse.ActionType.KEEP, null, null, 
                    "Failed to parse LLM response: " + e.getMessage());
        }
    }
}