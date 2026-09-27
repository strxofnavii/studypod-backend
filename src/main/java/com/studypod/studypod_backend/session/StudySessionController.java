package com.studypod.studypod_backend.session;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studypod.studypod_backend.user.User;
import com.studypod.studypod_backend.user.UserRepository;

@RestController
@RequestMapping("/sessions")
public class StudySessionController {

    @Autowired
    private StudySessionRepository sessionRepository;

    @Autowired
    private UserRepository userRepository;

    // =========================================================
    // GET MY SESSIONS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<StudySession>> getMySessions(
            Authentication authentication
    ) {

        String userId = (String) authentication.getPrincipal();

        return ResponseEntity.ok(
                sessionRepository.findByUserIdOrderByStartTimeDesc(userId)
        );
    }

    // =========================================================
    // START SESSION
    // =========================================================

    @PostMapping("/start")
    public ResponseEntity<StudySession> startSession(
            @RequestBody StudySession request,
            Authentication authentication
    ) {

        String userId = (String) authentication.getPrincipal();

        StudySession session = new StudySession();

        // Never trust the userId sent by the frontend.
        // Always use the authenticated user.
        session.setUserId(userId);

        session.setStartTime(LocalDateTime.now());

        String type = request.getType();

        if (type == null || type.isBlank()) {
            type = "FOCUS";
        }

        session.setType(type.toUpperCase());

        // A newly started session has no completed duration yet.
        session.setDurationMinutes(0);

        StudySession saved = sessionRepository.save(session);

        return ResponseEntity.ok(saved);
    }

    // =========================================================
    // END SESSION
    // =========================================================

    @PutMapping("/{id}/end")
    public ResponseEntity<?> endSession(
            @PathVariable String id,
            Authentication authentication
    ) {

        String userId = (String) authentication.getPrincipal();

        Optional<StudySession> optionalSession =
                sessionRepository.findById(id);

        if (optionalSession.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        StudySession session = optionalSession.get();

        // -----------------------------------------------------
        // SECURITY CHECK
        // -----------------------------------------------------

        if (!session.getUserId().equals(userId)) {
            return ResponseEntity
                    .status(403)
                    .body("Not your session");
        }

        // -----------------------------------------------------
        // DON'T END THE SAME SESSION TWICE
        // -----------------------------------------------------

        if (session.getEndTime() != null) {
            return ResponseEntity
                    .badRequest()
                    .body("Session has already ended");
        }

        // -----------------------------------------------------
        // END SESSION
        // -----------------------------------------------------

        LocalDateTime now = LocalDateTime.now();

        session.setEndTime(now);

        long durationSeconds = Duration.between(
                session.getStartTime(),
                now
        ).getSeconds();

        /*
         * Convert seconds to minutes.
         *
         * We round UP so that:
         *
         * 10 seconds -> 1 minute
         * 30 seconds -> 1 minute
         * 59 seconds -> 1 minute
         * 60 seconds -> 1 minute
         * 61 seconds -> 2 minutes
         */
        int durationMinutes = (int) Math.max(
                1,
                Math.ceil(durationSeconds / 60.0)
        );

        session.setDurationMinutes(durationMinutes);

        StudySession saved = sessionRepository.save(session);

        return ResponseEntity.ok(saved);
    }

    // =========================================================
    // PROFILE STATS
    // =========================================================

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(
            Authentication authentication
    ) {

        String userId = (String) authentication.getPrincipal();

        /*
         * IMPORTANT:
         *
         * Only FOCUS sessions are used for profile statistics.
         *
         * BREAK sessions should NOT:
         * - increase total focus
         * - increase completed sessions
         * - increase streak
         */
        List<StudySession> sessions =
                sessionRepository
                        .findByUserIdAndTypeOrderByStartTimeDesc(
                                userId,
                                "FOCUS"
                        );

        // -----------------------------------------------------
        // ONLY COMPLETED FOCUS SESSIONS
        // -----------------------------------------------------

        List<StudySession> completedSessions =
                sessions.stream()
                        .filter(session -> session.getEndTime() != null)
                        .toList();

        // -----------------------------------------------------
        // TOTAL FOCUS MINUTES
        // -----------------------------------------------------

        int totalFocusMinutes =
                completedSessions.stream()
                        .mapToInt(StudySession::getDurationMinutes)
                        .sum();

        // -----------------------------------------------------
        // COMPLETED SESSIONS
        // -----------------------------------------------------

        int completedSessionCount =
                completedSessions.size();

        // -----------------------------------------------------
        // CURRENT STREAK
        // Based on consecutive login days, not focus sessions —
        // tracked on the User entity and updated at login time.
        // -----------------------------------------------------

        // NOTE: `userId` here is actually the authenticated principal, which
        // is the User's internal primary key (users.id) — the JWT subject is
        // set to user.getId(), not user.getUserId(). Look the user up with
        // findById, not findByUserId (which searches the human-chosen login
        // handle and would never match, always falling back to 0).
        int currentStreak =
                userRepository.findById(userId)
                        .map(User::getLoginStreak)
                        .orElse(0);

        // -----------------------------------------------------
        // THIS WEEK'S FOCUS
        // -----------------------------------------------------

        List<Map<String, Object>> weeklyFocus =
                calculateWeeklyFocus(completedSessions);

        // -----------------------------------------------------
        // DAILY FOCUS (for heatmap)
        // -----------------------------------------------------

        List<Map<String, Object>> dailyFocus =
                calculateDailyFocus(completedSessions);

        // -----------------------------------------------------
        // RESPONSE
        // -----------------------------------------------------

        Map<String, Object> stats =
                new LinkedHashMap<>();

        stats.put(
                "totalFocusMinutes",
                totalFocusMinutes
        );

        stats.put(
                "completedSessions",
                completedSessionCount
        );

        stats.put(
                "currentStreak",
                currentStreak
        );

        stats.put(
                "weeklyFocus",
                weeklyFocus
        );

        stats.put(
                "dailyFocus",
                dailyFocus
        );

        return ResponseEntity.ok(stats);
    }
    // =========================================================
    // WEEKLY FOCUS
    // =========================================================

    private List<Map<String, Object>> calculateWeeklyFocus(
            List<StudySession> sessions
    ) {

        /*
         * Create all seven days with 0 minutes.
         */
        Map<DayOfWeek, Integer> minutesByDay =
                new EnumMap<>(DayOfWeek.class);

        for (DayOfWeek day : DayOfWeek.values()) {
            minutesByDay.put(day, 0);
        }

        LocalDate today = LocalDate.now();

        /*
         * Find Monday of the current week.
         */
        LocalDate monday =
                today.with(DayOfWeek.MONDAY);

        /*
         * Find Sunday of the current week.
         */
        LocalDate sunday =
                monday.plusDays(6);

        // -----------------------------------------------------
        // ADD SESSION MINUTES TO THE CORRECT DAY
        // -----------------------------------------------------

        for (StudySession session : sessions) {

            if (session.getEndTime() == null) {
                continue;
            }

            LocalDate date =
                    session.getEndTime().toLocalDate();

            /*
             * Ignore sessions outside the current week.
             */
            if (date.isBefore(monday)
                    || date.isAfter(sunday)) {
                continue;
            }

            DayOfWeek day =
                    date.getDayOfWeek();

            int existingMinutes =
                    minutesByDay.get(day);

            minutesByDay.put(
                    day,
                    existingMinutes
                            + session.getDurationMinutes()
            );
        }

        // -----------------------------------------------------
        // BUILD RESPONSE FOR FRONTEND
        // -----------------------------------------------------

        List<Map<String, Object>> result =
                new ArrayList<>();

        /*
         * Monday -> Sunday
         */
        for (DayOfWeek day : DayOfWeek.values()) {

            Map<String, Object> entry =
                    new LinkedHashMap<>();

            entry.put(
                    "day",
                    getShortDayName(day)
            );

            entry.put(
                    "minutes",
                    minutesByDay.get(day)
            );

            result.add(entry);
        }

        return result;
    }

    // =========================================================
    // DAILY FOCUS (last 365 days, for heatmap)
    // =========================================================

    private List<Map<String, Object>> calculateDailyFocus(
            List<StudySession> sessions
    ) {

        LocalDate today = LocalDate.now();

        // 365 days total, including today
        LocalDate startDate = today.minusDays(364);

        /*
         * Sum durationMinutes per calendar date, based on endTime,
         * for the last 365 days only.
         */
        Map<LocalDate, Integer> minutesByDate =
                new LinkedHashMap<>();

        for (StudySession session : sessions) {

            if (session.getEndTime() == null) {
                continue;
            }

            LocalDate date =
                    session.getEndTime().toLocalDate();

            if (date.isBefore(startDate)
                    || date.isAfter(today)) {
                continue;
            }

            int existingMinutes =
                    minutesByDate.getOrDefault(date, 0);

            minutesByDate.put(
                    date,
                    existingMinutes
                            + session.getDurationMinutes()
            );
        }

        // -----------------------------------------------------
        // BUILD RESPONSE FOR FRONTEND
        // Only include days that actually have focus time —
        // the frontend already treats missing dates as 0.
        // -----------------------------------------------------

        List<Map<String, Object>> result =
                new ArrayList<>();

        for (Map.Entry<LocalDate, Integer> entry :
                minutesByDate.entrySet()) {

            Map<String, Object> dayEntry =
                    new LinkedHashMap<>();

            dayEntry.put(
                    "date",
                    entry.getKey().toString() // "YYYY-MM-DD"
            );

            dayEntry.put(
                    "minutes",
                    entry.getValue()
            );

            result.add(dayEntry);
        }

        return result;
    }

    // =========================================================
    // DAY NAME
    // =========================================================

    private String getShortDayName(
            DayOfWeek day
    ) {

        return switch (day) {

            case MONDAY -> "Mon";

            case TUESDAY -> "Tue";

            case WEDNESDAY -> "Wed";

            case THURSDAY -> "Thu";

            case FRIDAY -> "Fri";

            case SATURDAY -> "Sat";

            case SUNDAY -> "Sun";
        };
    }
}