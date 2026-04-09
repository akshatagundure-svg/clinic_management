
package com.example.springcrud.controller;

import com.example.springcrud.model.ChatRequest;
import com.example.springcrud.model.ChatResponse;
import com.example.springcrud.service.AkshataAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/akshata-ai")
@CrossOrigin(origins = "*")
public class AkshataAIController {

    @Autowired
    private AkshataAIService akshataAIService;

    /**
     * POST /akshata-ai/chat
     * Body: { "message": "your question here" }
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResponse response = akshataAIService.chat(request.getMessage());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /akshata-ai/health
     * Check if Akshata AI service is running
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Akshata AI is running!");
    }
}