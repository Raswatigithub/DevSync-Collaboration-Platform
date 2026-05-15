package com.devsync.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserRepository users;

  @GetMapping("/me")
  UserResponse me(@AuthenticationPrincipal User user) {
    return UserResponse.from(user);
  }

  @PatchMapping("/me")
  UserResponse update(@AuthenticationPrincipal User user, @Valid @RequestBody UpdateProfileRequest request) {
    user.setBio(request.bio());
    user.setSkills(request.skills() == null ? user.getSkills() : request.skills());
    user.setUpdatedAt(Instant.now());
    return UserResponse.from(users.save(user));
  }
}
