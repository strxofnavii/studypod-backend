package com.studypod.studypod_backend.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping
    public ResponseEntity<List<Task>> getMyTasks(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        List<Task> tasks = taskRepository.findByUserId(userId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        task.setUserId(userId);
        Task saved = taskRepository.save(task);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable String id, @RequestBody Task updatedTask, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        return taskRepository.findById(id)
                .map(task -> {
                    if (!task.getUserId().equals(userId)) {
                        return ResponseEntity.status(403).body("Not your task");
                    }
                    task.setTitle(updatedTask.getTitle());
                    task.setDescription(updatedTask.getDescription());
                    task.setDueDate(updatedTask.getDueDate());
                    task.setStatus(updatedTask.getStatus());
                    Task saved = taskRepository.save(task);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable String id, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        return taskRepository.findById(id)
                .map(task -> {
                    if (!task.getUserId().equals(userId)) {
                        return ResponseEntity.status(403).body("Not your task");
                    }
                    taskRepository.delete(task);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}