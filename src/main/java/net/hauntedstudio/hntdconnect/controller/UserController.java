package net.hauntedstudio.hntdconnect.controller;

import net.hauntedstudio.hntdconnect.dto.StatusResponse;
import net.hauntedstudio.hntdconnect.dto.user.OrganizationSummary;
import net.hauntedstudio.hntdconnect.dto.user.UserResponse;
import net.hauntedstudio.hntdconnect.services.JwtService;
import net.hauntedstudio.hntdconnect.services.OrganizationService;
import net.hauntedstudio.hntdconnect.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final OrganizationService organizationService;
    private final UserService userService;
    private final JwtService jwtService;

    public UserController(OrganizationService organizationService, UserService userService, JwtService jwtService) {
        this.organizationService = organizationService;
        this.userService = userService;
        this.jwtService = jwtService;
    }


    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        String userUuid = jwtService.getUserUuidFromToken(token);
        var user = userService.findByUuid(userUuid);
        var orgs = organizationService.findAllByMemberUuid(userUuid);

        if (user.isEmpty()) {
            StatusResponse response = new StatusResponse(404, "User not found");
            return ResponseEntity.status(404).body(response);
        }

        UserResponse response = new UserResponse(
                user.get().getUsername(),
                user.get().getUuid(),
                user.get().getEmail(),
                orgs.stream()
                        .map(org -> {
                            var summary = new OrganizationSummary();
                            summary.setUuid(org.getUuid());
                            summary.setName(org.getName());
                            return summary;
                        })
                        .toList()
        );
        return ResponseEntity.ok(response);
    }
}
