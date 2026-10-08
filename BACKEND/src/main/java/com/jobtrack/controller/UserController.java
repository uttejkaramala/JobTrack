package com.jobtrack.controller;

import com.jobtrack.dto.auth.UserResponse;
import com.jobtrack.dto.user.ChangePasswordRequest;
import com.jobtrack.dto.user.UpdateUserRequest;
import com.jobtrack.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // =========================
    // GET PROFILE
    // =========================

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {

        return ResponseEntity.ok(
                userService.getCurrentUser()
        );
    }

    // =========================
    // UPDATE PROFILE
    // =========================

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody UpdateUserRequest request) {

        return ResponseEntity.ok(
                userService.updateProfile(request)
        );
    }

    // =========================
    // CHANGE PASSWORD
    // =========================

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(request);

        return ResponseEntity.noContent().build();
    }
}