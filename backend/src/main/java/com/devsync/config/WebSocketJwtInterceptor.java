package com.devsync.config;

import com.devsync.security.JwtService;
import com.devsync.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketJwtInterceptor implements ChannelInterceptor {
  private final JwtService jwtService;
  private final UserRepository users;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    if (accessor == null || accessor.getCommand() != StompCommand.CONNECT) {
      return message;
    }

    String header = accessor.getFirstNativeHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      users.findById(jwtService.parseUserId(header.substring(7))).ifPresent(user ->
        accessor.setUser(new UsernamePasswordAuthenticationToken(user, null, List.of()))
      );
    }
    return message;
  }
}
