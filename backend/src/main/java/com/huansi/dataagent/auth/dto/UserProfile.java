package com.huansi.dataagent.auth.dto;

import com.huansi.dataagent.user.User;

public record UserProfile(Long id, String username, String displayName, String email, String role) {

    public static UserProfile from(User user) {
        return new UserProfile(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getRole().name());
    }
}
