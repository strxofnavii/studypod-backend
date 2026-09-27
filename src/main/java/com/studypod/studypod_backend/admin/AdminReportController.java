package com.studypod.studypod_backend.admin;

import com.studypod.studypod_backend.report.Report;
import com.studypod.studypod_backend.report.ReportRepository;
import com.studypod.studypod_backend.report.ReportStatusUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/reports")
public class AdminReportController {

    @Autowired
    private ReportRepository reportRepository;

    @GetMapping
    public ResponseEntity<List<Report>> listReports(@RequestParam(required = false) String status) {
        List<Report> reports = (status == null || status.isBlank())
                ? reportRepository.findAllByOrderByCreatedAtDesc()
                : reportRepository.findByStatusOrderByCreatedAtDesc(status.toUpperCase());

        return ResponseEntity.ok(reports);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable String id,
            @RequestBody ReportStatusUpdateRequest request
    ) {
        Optional<Report> optionalReport = reportRepository.findById(id);
        if (optionalReport.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String status = request.getStatus();
        if (!"OPEN".equals(status) && !"IN_PROGRESS".equals(status) && !"RESOLVED".equals(status)) {
            return ResponseEntity.badRequest().body("Status must be OPEN, IN_PROGRESS or RESOLVED");
        }

        Report report = optionalReport.get();
        report.setStatus(status);

        if (request.getAdminNote() != null) {
            report.setAdminNote(request.getAdminNote());
        }

        report.setResolvedAt("RESOLVED".equals(status) ? LocalDateTime.now() : null);

        return ResponseEntity.ok(reportRepository.save(report));
    }
}