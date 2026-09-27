package com.studypod.studypod_backend.room.dto;

import java.time.LocalDateTime;

public class RoomMemberResponse {
    private String userId;
    private String name;
    private int avatarIndex;
    private String role;
    private LocalDateTime joinedAt;

    public RoomMemberResponse(String userId, String name, int avatarIndex, String role, LocalDateTime joinedAt) {
        this.userId = userId;
        this.name = name;
        this.avatarIndex = avatarIndex;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public int getAvatarIndex() {
        return avatarIndex;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}