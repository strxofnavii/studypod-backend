package com.studypod.studypod_backend.announcement;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, String> {
    List<Announcement> findByActiveTrueOrderByCreatedAtDesc();
    List<Announcement> findAllByOrderByCreatedAtDesc();
    long countByActiveTrue();
}