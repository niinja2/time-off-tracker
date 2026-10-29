package com.example.timeoff.api;

import com.example.timeoff.security.AuthService;
import com.example.timeoff.security.CurrentUser;
import com.example.timeoff.security.LoginResult;
import com.example.timeoff.security.UserView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class AuthController {

    private final AuthService authService;
    private final CurrentUser currentUser;

    AuthController(AuthService authService, CurrentUser currentUser) {
        this.authService = authService;
        this.currentUser = currentUser;
    }

    @PostMapping("/auth/login")
    LoginResult login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.username(), request.password());
    }

    @GetMapping("/me")
    UserView me() {
        Long userId = currentUser.id();

        return authService.me(userId);
    }

    record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }
}
