package com.studypod.studypod_backend.room;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/rooms/{roomId}/notes")
public class RoomNoteController {

    @Autowired
    private RoomNoteRepository roomNoteRepository;

    @Autowired
    private RoomService roomService;

    // ---------- Get all shared notes ----------

    @GetMapping
    public ResponseEntity<?> getNotes(@PathVariable String roomId, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        if (!roomService.isActiveMember(roomId, userId)) {
            return ResponseEntity.status(403).body("You are not a member of this room");
        }

        List<RoomNote> notes = roomNoteRepository.findByRoomIdOrderByUpdatedAtDesc(roomId);
        return ResponseEntity.ok(notes);
    }

    // ---------- Create a note ----------

    @PostMapping
    public ResponseEntity<?> createNote(
            @PathVariable String roomId,
            @RequestBody RoomNote note,
            Authentication authentication
    ) {
        String userId = (String) authentication.getPrincipal();

        if (!roomService.isActiveMember(roomId, userId)) {
            return ResponseEntity.status(403).body("You are not a member of this room");
        }

        if (note.getTitle() == null || note.getTitle().isBlank()) {
            return ResponseEntity.badRequest().body("Note title is required");
        }

        note.setRoomId(roomId);
        note.setCreatedBy(userId);
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());

        RoomNote saved = roomNoteRepository.save(note);
        return ResponseEntity.ok(saved);
    }

    // ---------- Update a note ----------

    @PutMapping("/{noteId}")
    public ResponseEntity<?> updateNote(
            @PathVariable String roomId,
            @PathVariable String noteId,
            @RequestBody RoomNote updatedNote,
            Authentication authentication
    ) {
        String userId = (String) authentication.getPrincipal();

        if (!roomService.isActiveMember(roomId, userId)) {
            return ResponseEntity.status(403).body("You are not a member of this room");
        }

        return roomNoteRepository.findById(noteId)
                .map(note -> {
                    if (!note.getRoomId().equals(roomId)) {
                        return ResponseEntity.status(404).body("Note not found in this room");
                    }
                    note.setTitle(updatedNote.getTitle());
                    note.setContent(updatedNote.getContent());
                    note.setUpdatedAt(LocalDateTime.now());
                    RoomNote saved = roomNoteRepository.save(note);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ---------- Delete a note ----------

    @DeleteMapping("/{noteId}")
    public ResponseEntity<?> deleteNote(
            @PathVariable String roomId,
            @PathVariable String noteId,
            Authentication authentication
    ) {
        String userId = (String) authentication.getPrincipal();

        if (!roomService.isActiveMember(roomId, userId)) {
            return ResponseEntity.status(403).body("You are not a member of this room");
        }

        return roomNoteRepository.findById(noteId)
                .map(note -> {
                    if (!note.getRoomId().equals(roomId)) {
                        return ResponseEntity.status(404).body("Note not found in this room");
                    }
                    roomNoteRepository.delete(note);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}