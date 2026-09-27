package com.studypod.studypod_backend.report;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String reporterId;

    @Column(nullable = false)
    private String reporterName;

    @Column(nullable = false)
    private String category = "OTHER"; // BUG, ABUSE, FEEDBACK, OTHER

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private String status = "OPEN"; // OPEN, IN_PROGRESS, RESOLVED

    @Column(length = 2000)
    private String adminNote;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime resolvedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getReporterId() { return reporterId; }
    public void setReporterId(String reporterId) { this.reporterId = reporterId; }
    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}