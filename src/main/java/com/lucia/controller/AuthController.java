package com.lucia.controller;

import com.lucia.dto.AuthResponse;
import com.lucia.dto.LoginRequest;
import com.lucia.dto.RegisterRequest;
import com.lucia.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            logger.info("Registration request for email: {}", request.getEmail());
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Registration failed for email: {}", request.getEmail(), e);
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            logger.info("Login request for email: {}", request.getEmail());
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login failed for email: {}", request.getEmail(), e);
            throw e;
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refresh_token");
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            AuthResponse response = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Token refresh failed", e);
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        try {
            authService.logout();
            return ResponseEntity.ok(Map.of("message", "Logout exitoso"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("message", "Logout exitoso"));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "auth"));
    }

    /**
     * Endpoint de prueba para verificar la respuesta de login con referral_code
     */
    @PostMapping("/test-login")
    public ResponseEntity<Map<String, Object>> testLogin(@Valid @RequestBody LoginRequest request) {
        try {
            logger.info("Test login request for email: {}", request.getEmail());
            AuthResponse response = authService.login(request);
            
            Map<String, Object> testResponse = Map.of(
                "message", "Login exitoso",
                "user_id", response.getUserId() != null ? response.getUserId() : "N/A",
                "email", response.getEmail() != null ? response.getEmail() : "N/A",
                "role", response.getRole() != null ? response.getRole() : "N/A",
                "referral_code", response.getReferralCode() != null ? response.getReferralCode() : "N/A",
                "has_access_token", response.getAccessToken() != null,
                "has_refresh_token", response.getRefreshToken() != null
            );
            
            return ResponseEntity.ok(testResponse);
        } catch (Exception e) {
            logger.error("Test login failed for email: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Error en test login: " + e.getMessage()));
        }
    }
}


