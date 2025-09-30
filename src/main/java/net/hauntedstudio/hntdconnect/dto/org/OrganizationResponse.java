package net.hauntedstudio.hntdconnect.dto.org;

import java.util.Set;

public record OrganizationResponse(
        String uuid,
        String name,
        String description,
        String website,
        String contactEmail,
        String ownerUUID,
        Set<String> memberUUIDs
) {
}