package com.studypod.studypod_backend.report;

import com.studypod.studypod_backend.user.User;
import com.studypod.studypod_backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestBody ReportRequest request,
            Authentication authentication
    ) {
        String userId = (String) authentication.getPrincipal();

        if (request.getSubject() == null || request.getSubject().isBlank()
                || request.getDescription() == null || request.getDescription().isBlank()) {
            return ResponseEntity.badRequest().body("Subject and description are required");
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Report report = new Report();
        report.setReporterId(user.getId());
        report.setReporterName(user.getName());
        report.setSubject(request.getSubject());
        report.setDescription(request.getDescription());

        String category = request.getCategory();
        report.setCategory(category == null || category.isBlank() ? "OTHER" : category.toUpperCase());

        return ResponseEntity.ok(reportRepository.save(report));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<Report>> getMyReports(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        return ResponseEntity.ok(reportRepository.findByReporterIdOrderByCreatedAtDesc(userId));
    }
}