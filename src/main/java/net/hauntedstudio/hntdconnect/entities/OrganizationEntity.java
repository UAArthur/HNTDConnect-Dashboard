package net.hauntedstudio.hntdconnect.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class OrganizationEntity {
    @jakarta.persistence.Id
    private String uuid;
    private String name;
    private String description;
    private String website;
    private String contactEmail;
    private String ownerUUID;
    @ManyToMany
    @JoinTable(
            name = "organization_members",
            joinColumns = @JoinColumn(name = "organization_uuid"),
            inverseJoinColumns = @JoinColumn(name = "member_uuid")
    )
    private Set<UserEntity> members;
}
