package com.lucia.controller;

import com.lucia.dto.RegisterRequest;
import com.lucia.service.SupabaseAuthService;
import com.lucia.service.ProfileService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final SupabaseAuthService supabaseAuthService;
    private final ProfileService profileService;

    public UserController(SupabaseAuthService supabaseAuthService, ProfileService profileService) {
        this.supabaseAuthService = supabaseAuthService;
        this.profileService = profileService;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody RegisterRequest request) {
        try {
            logger.info("Creating user with email: {}", request.getEmail());
            
            // Guard: do not create if email already exists in Supabase
            try {
                if (supabaseAuthService.userExistsByEmail(request.getEmail())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "El email ya existe"));
                }
            } catch (Exception ignored) {}

            // Create user in Supabase Authentication
            var authResponse = supabaseAuthService.signUp(request);

            // Upsert profile with role (default buyer if null/invalid)
            if (authResponse.getUserId() != null) {
                try {
                    java.util.UUID userId = java.util.UUID.fromString(authResponse.getUserId());
                    profileService.upsertProfile(userId, request.getRole());
                } catch (IllegalArgumentException ignore) {
                    // If Supabase returns non-UUID (shouldn't happen), skip profile creation
                }
            }
            
            Map<String, Object> response = Map.of(
                "message", "Usuario creado exitosamente",
                "email", request.getEmail(),
                "user_id", authResponse.getUserId() != null ? authResponse.getUserId() : "N/A",
                "status", "pending_confirmation"
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Failed to create user with email: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Error al crear usuario: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "users"));
    }
}
