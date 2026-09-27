package com.studypod.studypod_backend.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudySessionRepository extends JpaRepository<StudySession, String> {

    List<StudySession> findByUserIdOrderByStartTimeDesc(String userId);

    List<StudySession> findByUserIdAndTypeOrderByStartTimeDesc(
            String userId,
            String type
    );

    // =========================================================
    // ADMIN / SYSTEM-WIDE ANALYTICS
    // =========================================================

    @Query(value =
            "SELECT COALESCE(SUM(s.duration_minutes), 0) " +
            "FROM study_sessions s " +
            "WHERE s.type = 'FOCUS' AND s.end_time IS NOT NULL",
            nativeQuery = true)
    Long sumTotalFocusMinutes();

    @Query(value =
            "SELECT s.type AS type, COALESCE(SUM(s.duration_minutes), 0) AS totalMinutes " +
            "FROM study_sessions s " +
            "WHERE s.end_time IS NOT NULL " +
            "GROUP BY s.type",
            nativeQuery = true)
    List<SessionTypeRow> findSessionTypeBreakdown();

    @Query(value =
            "SELECT date(s.end_time) AS day, COALESCE(SUM(s.duration_minutes), 0) AS totalMinutes " +
            "FROM study_sessions s " +
            "WHERE s.type = 'FOCUS' AND s.end_time IS NOT NULL " +
            "AND date(s.end_time) >= date('now', '-29 days') " +
            "GROUP BY date(s.end_time) " +
            "ORDER BY day ASC",
            nativeQuery = true)
    List<DailyFocusRow> findSystemDailyFocusLast30Days();

    @Query(value =
            "SELECT COUNT(DISTINCT s.user_id) " +
            "FROM study_sessions s " +
            "WHERE s.type = 'FOCUS' AND s.end_time IS NOT NULL " +
            "AND date(s.end_time) = date('now')",
            nativeQuery = true)
    long countDistinctActiveUsersToday();
}