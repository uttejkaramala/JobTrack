package com.jobtrack.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobtrack.dto.auth.RegisterRequest;
import com.jobtrack.dto.auth.UserResponse;
import com.jobtrack.dto.user.ChangePasswordRequest;
import com.jobtrack.dto.user.UpdateUserRequest;
import com.jobtrack.entity.User;
import com.jobtrack.exception.DuplicateResourceException;
import com.jobtrack.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            CurrentUserService currentUserService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
    }

    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email is already registered"
            );
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        user.setPassword(encodedPassword);

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );
    }
    
    public UserResponse getCurrentUser() {

        User user = currentUserService.getCurrentUser();

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
    // =========================
    // UPDATE PROFILE
    // =========================

    public UserResponse updateProfile(
            UpdateUserRequest request) {

        User user = currentUserService.getCurrentUser();

        // Check whether email is being changed
        if (!user.getEmail().equalsIgnoreCase(request.getEmail())) {

            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException(
                        "Email is already registered"
                );
            }

            user.setEmail(request.getEmail());
        }

        user.setName(request.getName());

        User updatedUser = userRepository.save(user);

        return mapToResponse(updatedUser);
    }

    // =========================
    // CHANGE PASSWORD
    // =========================

    public void changePassword(
            ChangePasswordRequest request) {

        User user = currentUserService.getCurrentUser();

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getCurrentPassword(),
                        user.getPassword()
                );

        if (!passwordMatches) {
            throw new IllegalArgumentException(
                    "Current password is incorrect"
            );
        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException(
                    "New password must be different from current password"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);
    }

    // =========================
    // MAPPING
    // =========================

    private UserResponse mapToResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }
}