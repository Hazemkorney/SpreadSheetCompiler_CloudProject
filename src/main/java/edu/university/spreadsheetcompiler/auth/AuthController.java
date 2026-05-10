package edu.university.spreadsheetcompiler.auth;

import edu.university.spreadsheetcompiler.auth.AuthDtos.AuthResponse;
import edu.university.spreadsheetcompiler.auth.AuthDtos.LoginRequest;
import edu.university.spreadsheetcompiler.auth.AuthDtos.RegisterRequest;
import edu.university.spreadsheetcompiler.auth.AuthDtos.UserResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
