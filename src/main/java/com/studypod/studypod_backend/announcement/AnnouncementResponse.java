package com.studypod.studypod_backend.announcement;

import java.time.LocalDateTime;

public class AnnouncementResponse {

    private String id;
    private String title;
    private String message;
    private String createdBy;
    private LocalDateTime createdAt;
    private boolean active;

    public static AnnouncementResponse from(Announcement announcement) {

        AnnouncementResponse response =
                new AnnouncementResponse();

        response.id = announcement.getId();
        response.title = announcement.getTitle();
        response.message = announcement.getMessage();
        response.createdBy = announcement.getCreatedBy();
        response.createdAt = announcement.getCreatedAt();
        response.active = announcement.isActive();

        return response;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isActive() {
        return active;
    }
}