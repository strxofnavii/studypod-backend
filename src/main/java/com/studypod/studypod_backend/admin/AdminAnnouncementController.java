package com.studypod.studypod_backend.admin;

import com.studypod.studypod_backend.announcement.Announcement;
import com.studypod.studypod_backend.announcement.AnnouncementRepository;
import com.studypod.studypod_backend.announcement.AnnouncementRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/announcements")
public class AdminAnnouncementController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    /*
     * Get all announcements for admin.
     */
    @GetMapping
    public ResponseEntity<List<Announcement>> listAll() {

        return ResponseEntity.ok(
                announcementRepository
                        .findAllByOrderByCreatedAtDesc()
        );
    }

    /*
     * Create announcement.
     */
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody AnnouncementRequest request,
            Authentication authentication
    ) {

        String adminId =
                (String) authentication.getPrincipal();

        if (request.getTitle() == null ||
                request.getTitle().isBlank() ||
                request.getMessage() == null ||
                request.getMessage().isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body("Title and message are required");
        }

        Announcement announcement =
                new Announcement();

        announcement.setTitle(
                request.getTitle()
        );

        announcement.setMessage(
                request.getMessage()
        );

        announcement.setCreatedBy(
                adminId
        );

        announcement.setActive(
                request.getActive() == null ||
                request.getActive()
        );

        return ResponseEntity.ok(
                announcementRepository.save(
                        announcement
                )
        );
    }

    /*
     * Update announcement.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable String id,
            @RequestBody AnnouncementRequest request
    ) {

        Optional<Announcement> optional =
                announcementRepository.findById(id);

        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Announcement announcement =
                optional.get();

        if (request.getTitle() != null &&
                !request.getTitle().isBlank()) {

            announcement.setTitle(
                    request.getTitle()
            );
        }

        if (request.getMessage() != null &&
                !request.getMessage().isBlank()) {

            announcement.setMessage(
                    request.getMessage()
            );
        }

        if (request.getActive() != null) {

            announcement.setActive(
                    request.getActive()
            );
        }

        return ResponseEntity.ok(
                announcementRepository.save(
                        announcement
                )
        );
    }

    /*
     * Delete announcement.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable String id
    ) {

        if (!announcementRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        announcementRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }
}