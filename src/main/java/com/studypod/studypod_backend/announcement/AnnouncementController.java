package com.studypod.studypod_backend.announcement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    /*
     * Returns announcements that are currently active.
     * These are the announcements visible to students.
     */
    @GetMapping("/active")
    public ResponseEntity<List<AnnouncementResponse>> getActive() {

        List<AnnouncementResponse> response =
                announcementRepository
                        .findByActiveTrueOrderByCreatedAtDesc()
                        .stream()
                        .map(AnnouncementResponse::from)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}