package net.hauntedstudio.hntdconnect.dto.prdct;

public record ProductCreateRequest(
        String organizationId,
        String name,
        String description,
        ProductStatus status,
        String currentVersion,
        String platforms) {}