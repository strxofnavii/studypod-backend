package com.studypod.studypod_backend.room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms/{roomId}/tasks")
public class RoomTaskController {

    @Autowired
    private RoomTaskRepository roomTaskRepository;

    @Autowired
    private RoomService roomService;

    // ---------- Get task board ----------

    @GetMapping
    public ResponseEntity<?> getTasks(@PathVariable String roomId, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        if (!roomService.isActiveMember(roomId, userId)) {
            return ResponseEntity.status(403).body("You are not a member of this room");
        }

        List<RoomTask> tasks = roomTaskRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
        return ResponseEntity.ok(tasks);
    }

    // ---------- Create a task ----------

    @PostMapping
    public ResponseEntity<?> createTask(
            @PathVariable String roomId,
            @RequestBody RoomTask task,
            Authentication authentication
    ) {
        String userId = (String) authentication.getPrincipal();

        if (!roomService.isActiveMember(roomId, userId)) {
            return ResponseEntity.status(403).body("You are not a member of this room");
        }

        if (task.getTitle() == null || task.getTitle().isBlank()) {
            return ResponseEntity.badRequest().body("Task title is required");
        }

        task.setRoomId(roomId);
        task.setCreatedBy(userId);
        if (task.getStatus() == null || task.getStatus().isBlank()) {
            task.setStatus("TODO");
        }

        RoomTask saved = roomTaskRepository.save(task);
        return ResponseEntity.ok(saved);
    }

    // ---------- Update a task (title, description, due date, status) ----------

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(
            @PathVariable String roomId,
            @PathVariable String taskId,
            @RequestBody RoomTask updatedTask,
            Authentication authentication
    ) {
        String userId = (String) authentication.getPrincipal();

        if (!roomService.isActiveMember(roomId, userId)) {
            return ResponseEntity.status(403).body("You are not a member of this room");
        }

        return roomTaskRepository.findById(taskId)
                .map(task -> {
                    if (!task.getRoomId().equals(roomId)) {
                        return ResponseEntity.status(404).body("Task not found in this room");
                    }
                    task.setTitle(updatedTask.getTitle());
                    task.setDescription(updatedTask.getDescription());
                    task.setDueDate(updatedTask.getDueDate());
                    task.setStatus(updatedTask.getStatus());
                    RoomTask saved = roomTaskRepository.save(task);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ---------- Delete a task ----------

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(
            @PathVariable String roomId,
            @PathVariable String taskId,
            Authentication authentication
    ) {
        String userId = (String) authentication.getPrincipal();

        if (!roomService.isActiveMember(roomId, userId)) {
            return ResponseEntity.status(403).body("You are not a member of this room");
        }

        return roomTaskRepository.findById(taskId)
                .map(task -> {
                    if (!task.getRoomId().equals(roomId)) {
                        return ResponseEntity.status(404).body("Task not found in this room");
                    }
                    roomTaskRepository.delete(task);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
