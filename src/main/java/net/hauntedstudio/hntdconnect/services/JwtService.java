package net.hauntedstudio.hntdconnect.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import net.hauntedstudio.hntdconnect.entities.UserSessionEntity;
import net.hauntedstudio.hntdconnect.repositories.UserSessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private final UserSessionRepository sessionRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:3600000}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration:86400000}")
    private long refreshExpirationMs;

    public JwtService(UserSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Transactional
    public UserSessionEntity createSession(String userUuid) {
        sessionRepository.deactivateAllUserSessions(userUuid);

        String accessToken = generateToken(userUuid, jwtExpirationMs);
        String refreshToken = generateToken(userUuid, refreshExpirationMs);

        String hashedAccessToken = hashToken(accessToken);
        String hashedRefreshToken = hashToken(refreshToken);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime accessExpiry = now.plusSeconds(jwtExpirationMs / 1000);
        LocalDateTime refreshExpiry = now.plusSeconds(refreshExpirationMs / 1000);

        UserSessionEntity session = new UserSessionEntity(
                userUuid,
                hashedAccessToken,
                hashedRefreshToken,
                accessExpiry,
                refreshExpiry,
                accessToken,
                refreshToken
        );
        return sessionRepository.save(session);
    }


    private String generateToken(String userUuid, long expiration) {
        return Jwts.builder()
                .subject(userUuid)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String getUserUuidFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            String hashedToken = hashToken(token);
            return sessionRepository.findByAccessTokenAndActiveTrue(hashedToken).isPresent();
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Optional<UserSessionEntity> getActiveSession(String token) {
        return sessionRepository.findByAccessTokenAndActiveTrue(token);
    }

    public Optional<UserSessionEntity> getActiveSessionByRefreshToken(String refreshToken) {
        return sessionRepository.findByRefreshTokenAndActiveTrue(refreshToken);
    }

    @Transactional
    public void deactivateSession(String refreshToken) {
        sessionRepository.deactivateSessionByRefreshToken(refreshToken);
    }

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
