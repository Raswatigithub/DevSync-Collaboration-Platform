package com.devsync.forum;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, UUID> {
  Optional<Vote> findByUserIdAndTargetTypeAndTargetId(UUID userId, String targetType, UUID targetId);
}
