package com.studypod.studypod_backend.room.dto;

import java.time.LocalDateTime;

public class RoomResponse {
    private String id;
    private String name;
    private String description;
    private String joinCode;
    private String createdBy;
    private LocalDateTime createdAt;
    private int memberCount;
    private String myRole; // HOST or MEMBER, for the requesting user

    public RoomResponse(String id, String name, String description, String joinCode,
                         String createdBy, LocalDateTime createdAt, int memberCount, String myRole) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.joinCode = joinCode;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.memberCount = memberCount;
        this.myRole = myRole;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public String getMyRole() {
        return myRole;
    }
}