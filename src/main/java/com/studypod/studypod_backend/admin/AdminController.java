package com.studypod.studypod_backend.admin;

import com.studypod.studypod_backend.announcement.AnnouncementRepository;
import com.studypod.studypod_backend.report.ReportRepository;
import com.studypod.studypod_backend.room.RoomRepository;
import com.studypod.studypod_backend.session.StudySessionRepository;
import com.studypod.studypod_backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private StudySessionRepository sessionRepository;
    @Autowired private ReportRepository reportRepository;
    @Autowired private AnnouncementRepository announcementRepository;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();

        Long totalFocus = sessionRepository.sumTotalFocusMinutes();

        summary.put("totalUsers", userRepository.count());
        summary.put("totalAdmins", userRepository.countByRole("ADMIN"));
        summary.put("activeToday", userRepository.countByLastLoginDate(LocalDate.now()));
        summary.put("totalRooms", roomRepository.count());
        summary.put("totalFocusMinutes", totalFocus == null ? 0 : totalFocus);
        summary.put("openReports", reportRepository.countByStatus("OPEN"));
        summary.put("activeAnnouncements", announcementRepository.countByActiveTrue());

        return ResponseEntity.ok(summary);
    }
}