package net.hauntedstudio.hntdconnect.repositories;

import net.hauntedstudio.hntdconnect.entities.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSessionEntity, Long> {
    Optional<UserSessionEntity> findByAccessTokenAndActiveTrue(String accessToken);
    Optional<UserSessionEntity> findByRefreshTokenAndActiveTrue(String refreshToken);

    @Modifying
    @Query("UPDATE UserSessionEntity s SET s.active = false WHERE s.userUuid = :userUuid")
    void deactivateAllUserSessions(String userUuid);

    @Modifying
    @Query("UPDATE UserSessionEntity s SET s.active = false WHERE s.refreshToken = :refreshToken")
    void deactivateSessionByRefreshToken(String refreshToken);
}
