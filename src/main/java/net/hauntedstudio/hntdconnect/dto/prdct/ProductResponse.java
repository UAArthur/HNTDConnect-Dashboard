package net.hauntedstudio.hntdconnect.dto.prdct;

import java.time.LocalDateTime;

public record ProductResponse(
        String uuid,
        String organizationId,
        String name,
        String description,
        ProductStatus status,
        String currentVersion,
        LocalDateTime releaseDate,
        String platforms,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
