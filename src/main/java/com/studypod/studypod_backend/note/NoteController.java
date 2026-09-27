package com.studypod.studypod_backend.note;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    @GetMapping
    public ResponseEntity<List<Note>> getMyNotes(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(noteRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }

    @GetMapping("/folders")
    public ResponseEntity<List<String>> getMyFolders(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(noteRepository.findDistinctFoldersByUserId(userId));
    }

    @GetMapping("/by-folder")
    public ResponseEntity<List<Note>> getNotesByFolder(
            @RequestParam String folder,
            Authentication authentication
    ) {
        String userId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(
                noteRepository.findByUserIdAndFolderOrderByCreatedAtDesc(userId, folder)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<Note>> searchNotes(
            @RequestParam String q,
            Authentication authentication
    ) {
        String userId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(
                noteRepository.findByUserIdAndTitleContainingIgnoreCaseOrderByCreatedAtDesc(userId, q)
        );
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        note.setUserId(userId);
        note.setFolder(note.getFolder());
        Note saved = noteRepository.save(note);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateNote(@PathVariable String id, @RequestBody Note updatedNote, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        return noteRepository.findById(id)
                .map(note -> {
                    if (!note.getUserId().equals(userId)) {
                        return ResponseEntity.status(403).body("Not your note");
                    }
                    note.setTitle(updatedNote.getTitle());
                    note.setContent(updatedNote.getContent());
                    note.setFolder(updatedNote.getFolder());
                    Note saved = noteRepository.save(note);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable String id, Authentication authentication) {
        String userId = (String) authentication.getPrincipal();

        return noteRepository.findById(id)
                .map(note -> {
                    if (!note.getUserId().equals(userId)) {
                        return ResponseEntity.status(403).body("Not your note");
                    }
                    noteRepository.delete(note);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}