package net.hauntedstudio.hntdconnect.dto.user;

import lombok.Getter;

import java.sql.Struct;
import java.util.List;

public record UserResponse(
        String uuid,
        String username,
        String email,
        List<OrganizationSummary> organizations) {
}

