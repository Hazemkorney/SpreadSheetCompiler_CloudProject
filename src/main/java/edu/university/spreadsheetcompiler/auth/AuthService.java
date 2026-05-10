package edu.university.spreadsheetcompiler.auth;

import edu.university.spreadsheetcompiler.auth.AuthDtos.AuthResponse;
import edu.university.spreadsheetcompiler.auth.AuthDtos.LoginRequest;
import edu.university.spreadsheetcompiler.auth.AuthDtos.RegisterRequest;
import edu.university.spreadsheetcompiler.auth.AuthDtos.UserResponse;
import edu.university.spreadsheetcompiler.common.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final long expirationSeconds;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider,
                       @Value("${app.jwt.expiration-seconds}") long expirationSeconds) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.expirationSeconds = expirationSeconds;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) throw new ApiException("Username already used");
        if (userRepository.existsByEmail(request.email())) throw new ApiException("Email already used");
        UserEntity user = new UserEntity();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.STUDENT);
        userRepository.save(user);
        return new UserResponse(user.getId(), user.getUsername(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail())
                .orElseThrow(() -> new ApiException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ApiException("Invalid credentials");
        }
        return new AuthResponse(jwtProvider.generate(user), "Bearer", expirationSeconds);
    }
}
