package net.hauntedstudio.hntdconnect.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
@Getter
@Setter
@NoArgsConstructor
public class UserSessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userUuid;

    @Column(nullable = false, unique = true)
    private String accessToken;

    @Column(nullable = false, unique = true)
    private String refreshToken;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime accessTokenExpiresAt;

    @Column(nullable = false)
    private LocalDateTime refreshTokenExpiresAt;

    @Column(nullable = false)
    private boolean active = true;

    @Transient
    private String rawAccessToken;

    @Transient
    private String rawRefreshToken;


    public UserSessionEntity(String userUuid, String accessToken, String refreshToken,
                             LocalDateTime accessTokenExpiresAt, LocalDateTime refreshTokenExpiresAt,
                             String rawAccessToken, String rawRefreshToken) {
        this.userUuid = userUuid;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.refreshTokenExpiresAt = refreshTokenExpiresAt;
        this.rawAccessToken = rawAccessToken;
        this.rawRefreshToken = rawRefreshToken;
    }
}
