package com.studypod.studypod_backend.note;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/notes")
public class FileUploadController {

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            StringBuilder content = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)
            );
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();

            return ResponseEntity.ok(Map.of("content", content.toString()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Could not read file: " + e.getMessage());
        }
    }
}