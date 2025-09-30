package net.hauntedstudio.hntdconnect.services;

import net.hauntedstudio.hntdconnect.dto.auth.RegisterRequest;
import net.hauntedstudio.hntdconnect.entities.UserEntity;
import net.hauntedstudio.hntdconnect.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        UserEntity user = new UserEntity();
        user.setUuid(UUID.randomUUID().toString());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        return userRepository.save(user);
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<UserEntity> findByUuid(String uuid) {
        return userRepository.findByUuid(uuid);
    }

    public boolean validateCredentials(String username, String rawPassword) {
        System.out.println("Validating credentials for user: " + username);
        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return false;
        String hashedPassword = userOpt.get().getPassword();
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
