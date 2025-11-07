package com.cronos.bakery.presentation.controller.auth;

import com.cronos.bakery.application.dto.request.CreateUserRequest;
import com.cronos.bakery.application.dto.request.LoginRequest;
import com.cronos.bakery.application.dto.request.RefreshTokenRequest;
import com.cronos.bakery.application.dto.response.ApiResponse;
import com.cronos.bakery.application.dto.response.LoginResponse;
import com.cronos.bakery.application.dto.response.TokenResponse;
import com.cronos.bakery.application.dto.response.UserResponse;
import com.cronos.bakery.application.service.AuthenticationService;
import com.cronos.bakery.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Creates a new user account")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody CreateUserRequest request) {

        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));

    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates user and returns JWT tokens")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authenticationService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generates a new access token using refresh token")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = authenticationService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "User logout", description = "Revokes user's refresh tokens")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication, @RequestBody(required = false) RefreshTokenRequest request) {

        String refreshToken = request != null ? request.getRefreshToken() : null;
        authenticationService.logout(authentication.getName(), refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }
}
