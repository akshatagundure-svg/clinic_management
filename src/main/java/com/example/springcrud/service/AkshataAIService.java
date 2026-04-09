package com.example.springcrud.service;

import com.example.springcrud.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AkshataAIService {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    @Value("${deepseek.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public ChatResponse chat(String userMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // ✅ System message — forces English responses
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content",
            "You are Akshata AI, a helpful and professional AI assistant. " +
            "ALWAYS respond in English only, regardless of the language used in the question. " +
            "Be clear, concise, and friendly."
        );

        // User message
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", userMessage);

        // Build request body for DeepSeek API
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", List.of(systemMessage, message)); // ✅ system + user
        body.put("max_tokens", 1024);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            Map responseBody = response.getBody();

            // Parse DeepSeek response
            List<Map> choices = (List<Map>) responseBody.get("choices");
            Map firstChoice = choices.get(0);
            Map messageMap = (Map) firstChoice.get("message");
            String replyText = (String) messageMap.get("content");

            return new ChatResponse("Akshata AI", replyText, model);

        } catch (Exception e) {
            return new ChatResponse("Akshata AI", "Error: " + e.getMessage(), model);
        }
    }
}