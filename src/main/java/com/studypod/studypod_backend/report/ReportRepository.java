package com.studypod.studypod_backend.report;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, String> {
    List<Report> findByReporterIdOrderByCreatedAtDesc(String reporterId);
    List<Report> findAllByOrderByCreatedAtDesc();
    List<Report> findByStatusOrderByCreatedAtDesc(String status);
    long countByStatus(String status);
}