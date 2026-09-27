package com.studypod.studypod_backend.ai;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody AIGenerateRequest request) {
        String prompt = buildPrompt(request.getNoteContent(), request.getType());
        String result = geminiService.generateContent(prompt);
        return ResponseEntity.ok(Map.of("type", request.getType(), "content", result));
    }

    private String buildPrompt(String noteContent, String type) {
        return switch (type) {
            case "summary" -> "Summarize the following study notes in 2-3 concise sentences:\n\n" + noteContent;
            case "flashcards" -> "Create 3 flashcards (question and answer pairs) from these study notes. Format as 'Q: ... A: ...' on separate lines:\n\n" + noteContent;
            case "quiz" -> "Create 1 multiple-choice quiz question with 4 options from these study notes. Mark the correct answer clearly:\n\n" + noteContent;
            case "flowchart" -> "Extract the key process from these notes as exactly 3 to 5 steps. Respond with ONLY the steps, separated by the pipe character |, and nothing else. No numbering, no bold text, no asterisks, no introduction, no explanation. Example format: First step text|Second step text|Third step text\n\nNotes:\n" + noteContent;
            default -> "Summarize this:\n\n" + noteContent;
        };
    }
}