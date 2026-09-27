package com.studypod.studypod_backend.admin.dto;

import com.studypod.studypod_backend.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AdminUserResponse {
    private String id;
    private String userId;
    private String name;
    private String role;
    private String status;
    private int avatarIndex;
    private int loginStreak;
    private LocalDate lastLoginDate;
    private LocalDateTime createdAt;

    public static AdminUserResponse from(User user) {
        AdminUserResponse dto = new AdminUserResponse();
        dto.id = user.getId();
        dto.userId = user.getUserId();
        dto.name = user.getName();
        dto.role = user.getRole();
        dto.status = user.getStatus() == null ? "ACTIVE" : user.getStatus();
        dto.avatarIndex = user.getAvatarIndex();
        dto.loginStreak = user.getLoginStreak();
        dto.lastLoginDate = user.getLastLoginDate();
        dto.createdAt = user.getCreatedAt();
        return dto;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public int getAvatarIndex() { return avatarIndex; }
    public int getLoginStreak() { return loginStreak; }
    public LocalDate getLastLoginDate() { return lastLoginDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}