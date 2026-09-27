package com.studypod.studypod_backend.admin;

import com.studypod.studypod_backend.session.DailyFocusRow;
import com.studypod.studypod_backend.session.SessionTypeRow;
import com.studypod.studypod_backend.session.StudySessionRepository;
import com.studypod.studypod_backend.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/analytics")
public class AdminAnalyticsController {

    @Autowired
    private StudySessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAnalytics() {

        Map<String, Object> result = new LinkedHashMap<>();

        // Total focus minutes
        Long totalFocus = sessionRepository.sumTotalFocusMinutes();

        // Daily focus for the last 30 days
        List<Map<String, Object>> dailyFocus = new ArrayList<>();

        for (DailyFocusRow row : sessionRepository.findSystemDailyFocusLast30Days()) {

            Map<String, Object> entry = new LinkedHashMap<>();

            entry.put("date", row.getDay());
            entry.put("minutes", row.getTotalMinutes());

            dailyFocus.add(entry);
        }

        // Session type breakdown
        Map<String, Long> sessionTypeBreakdown = new LinkedHashMap<>();

        sessionTypeBreakdown.put("FOCUS", 0L);
        sessionTypeBreakdown.put("BREAK", 0L);

        for (SessionTypeRow row : sessionRepository.findSessionTypeBreakdown()) {

            sessionTypeBreakdown.put(
                    row.getType(),
                    row.getTotalMinutes()
            );
        }

        // General analytics
        result.put("totalUsers", userRepository.count());

        result.put(
                "totalFocusMinutes",
                totalFocus == null ? 0 : totalFocus
        );

        result.put(
                "activeToday",
                sessionRepository.countDistinctActiveUsersToday()
        );

        result.put("dailyFocus", dailyFocus);

        result.put(
                "sessionTypeBreakdown",
                sessionTypeBreakdown
        );

        return ResponseEntity.ok(result);
    }
}