package com.studypod.studypod_backend.room.dto;

import java.time.LocalDateTime;

public class RoomMessageResponse {
    private String id;
    private String userId;
    private String senderName;
    private int avatarIndex;
    private String content;
    private LocalDateTime createdAt;

    public RoomMessageResponse(String id, String userId, String senderName, int avatarIndex,
                               String content, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.senderName = senderName;
        this.avatarIndex = avatarIndex;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getSenderName() {
        return senderName;
    }

    public int getAvatarIndex() {
        return avatarIndex;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}