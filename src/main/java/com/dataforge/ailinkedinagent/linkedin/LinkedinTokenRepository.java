package com.dataforge.ailinkedinagent.linkedin;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LinkedinTokenRepository extends JpaRepository<LinkedinToken, Long> {
    Optional<LinkedinToken> findTopByProviderOrderByUpdatedAtDesc(String provider);
}
