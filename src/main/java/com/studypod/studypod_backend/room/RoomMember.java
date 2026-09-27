package com.studypod.studypod_backend.room;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "room_members",
    uniqueConstraints = @UniqueConstraint(columnNames = {"roomId", "userId"})
)
public class RoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String role = "MEMBER"; // HOST or MEMBER

    @Column(nullable = false)
    private boolean active = true;

    private LocalDateTime joinedAt = LocalDateTime.now();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}