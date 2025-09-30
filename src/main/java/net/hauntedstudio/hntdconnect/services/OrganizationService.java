package net.hauntedstudio.hntdconnect.services;

import net.hauntedstudio.hntdconnect.dto.org.CreateOrgRequest;
import net.hauntedstudio.hntdconnect.dto.org.OrganizationResponse;
import net.hauntedstudio.hntdconnect.entities.OrganizationEntity;
import net.hauntedstudio.hntdconnect.entities.UserEntity;
import net.hauntedstudio.hntdconnect.repositories.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final UserService userService;

    public OrganizationService(OrganizationRepository organizationRepository, UserService userService) {
        this.organizationRepository = organizationRepository;
        this.userService = userService;
    }

    public OrganizationEntity createOrganization(CreateOrgRequest request, String userUuid) {
        OrganizationEntity org = new OrganizationEntity();
        org.setUuid(UUID.randomUUID().toString());
        org.setName(request.name());
        org.setDescription("");
        org.setWebsite("");
        org.setContactEmail("");
        org.setOwnerUUID(userUuid);
        Set<UserEntity> members = new HashSet<>();
        members.add(userService.findByUuid(org.getOwnerUUID()).get());
        org.setMembers(members);
        return organizationRepository.save(org);
    }

    public OrganizationEntity findByUuid(String uuid) {
        return organizationRepository.findByUuidWithMembers(uuid)
                .orElse(null);
    }

    public java.util.List<OrganizationEntity> findAllByMemberUuid(String userUuid) {
        return organizationRepository.findAllByMemberUuid(userUuid);
    }

    public boolean isMember(String orgUuid, String userUuid) {
        UserEntity user = userService.findByUuid(userUuid).orElse(null);
        if (user == null) {
            return false;
        }
        return organizationRepository.existsByUuidAndMembersContains(orgUuid, user);
    }

    public OrganizationResponse toResponse(OrganizationEntity org) {
        Set<String> memberUUIDs = org.getMembers().stream()
                .map(UserEntity::getUuid)
                .collect(Collectors.toSet());
        return new OrganizationResponse(
                org.getUuid(),
                org.getName(),
                org.getDescription(),
                org.getWebsite(),
                org.getContactEmail(),
                org.getOwnerUUID(),
                memberUUIDs
        );
    }

}
