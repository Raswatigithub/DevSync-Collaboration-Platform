package com.devsync.auth;

import com.devsync.security.JwtService;
import com.devsync.users.User;
import com.devsync.users.UserRepository;
import com.devsync.users.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository users;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthResponse register(RegisterRequest request) {
    if (users.existsByEmail(request.email()) || users.existsByUsername(request.username())) {
      throw new IllegalArgumentException("Email or username already exists");
    }
    User user = new User();
    user.setUsername(request.username());
    user.setEmail(request.email().toLowerCase());
    user.setPasswordHash(passwordEncoder.encode(request.password()));
    User saved = users.save(user);
    return new AuthResponse(jwtService.generate(saved), UserResponse.from(saved));
  }

  public AuthResponse login(LoginRequest request) {
    User user = users.findByEmail(request.email().toLowerCase())
      .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new IllegalArgumentException("Invalid credentials");
    }
    return new AuthResponse(jwtService.generate(user), UserResponse.from(user));
  }
}
