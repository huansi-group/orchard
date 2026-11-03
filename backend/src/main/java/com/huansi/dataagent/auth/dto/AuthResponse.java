package com.huansi.dataagent.auth.dto;

public record AuthResponse(String token, String tokenType, long expiresIn, UserProfile user) {
}
