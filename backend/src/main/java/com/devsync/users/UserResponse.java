package com.devsync.users;

import java.util.List;
import java.util.UUID;

public record UserResponse(UUID id, String username, String email, String bio, List<String> skills) {
  public static UserResponse from(User user) {
    return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getBio(), user.getSkills());
  }
}
