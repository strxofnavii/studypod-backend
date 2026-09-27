package com.studypod.studypod_backend.user;

public class AuthResponse {
    private String token;
    private String id;
    private String name;
    private String userId;
    private int avatarIndex;

    public AuthResponse(String token, String id, String name, String userId, int avatarIndex) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.avatarIndex = avatarIndex;
    }

    public String getToken() {
        return token;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public int getAvatarIndex() {
        return avatarIndex;
    }
}