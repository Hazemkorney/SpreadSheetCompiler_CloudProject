package edu.university.spreadsheetcompiler.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {
    public record RegisterRequest(
            @NotBlank String username,
            @Email @NotBlank String email,
            @Size(min = 8) String password
    ) {}

    public record LoginRequest(@NotBlank String usernameOrEmail, @NotBlank String password) {}

    public record AuthResponse(String accessToken, String tokenType, long expiresIn) {}

    public record UserResponse(Long id, String username, String role) {}
}
