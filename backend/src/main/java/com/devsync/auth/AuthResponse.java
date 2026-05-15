package com.devsync.auth;

import com.devsync.users.UserResponse;

public record AuthResponse(String accessToken, UserResponse user) {
}
