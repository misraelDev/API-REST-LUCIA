package com.lucia.controller;

import com.lucia.dto.RegisterRequest;
import com.lucia.dto.UserUpdateDto;
import com.lucia.service.SupabaseAuthService;
import com.lucia.service.ProfileService;
import com.lucia.service.WebSocketNotificationService;
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
    private final WebSocketNotificationService webSocketNotificationService;

    public UserController(SupabaseAuthService supabaseAuthService, ProfileService profileService, WebSocketNotificationService webSocketNotificationService) {
        this.supabaseAuthService = supabaseAuthService;
        this.profileService = profileService;
        this.webSocketNotificationService = webSocketNotificationService;
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
            
            // Enviar notificación WebSocket sobre nuevo usuario creado
            try {
                Map<String, Object> userNotification = Map.of(
                    "type", "user_created",
                    "user_id", authResponse.getUserId() != null ? authResponse.getUserId() : "N/A",
                    "email", request.getEmail(),
                    "role", request.getRole() != null ? request.getRole() : "user",
                    "timestamp", System.currentTimeMillis()
                );
                webSocketNotificationService.notifyNewUser(userNotification);
                logger.info("Notificación WebSocket enviada para nuevo usuario: {}", request.getEmail());
            } catch (Exception e) {
                logger.warn("Error al enviar notificación WebSocket para nuevo usuario: {}", e.getMessage());
            }
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Failed to create user with email: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Error al crear usuario: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        try {
            logger.info("Retrieving all users");
            
            // Get all profiles from local database
            var profiles = profileService.getAllProfiles();
            
            // Get user information from Supabase for each profile
            var users = new java.util.ArrayList<Map<String, Object>>();
            for (var profile : profiles) {
                try {
                    var userInfo = supabaseAuthService.getUserById(profile.getId().toString());
                    Map<String, Object> user = Map.of(
                        "id", profile.getId().toString(),
                        "email", userInfo.getOrDefault("email", "N/A"),
                        "role", profile.getRole().name(),
                        "created_at", userInfo.getOrDefault("created_at", "N/A"),
                        "email_confirmed", userInfo.get("email_confirmed_at") != null
                    );
                    users.add(user);
                } catch (Exception e) {
                    logger.warn("Failed to get user info for profile ID: {}", profile.getId(), e);
                    // Add user with limited info if Supabase call fails
                    Map<String, Object> user = Map.of(
                        "id", profile.getId().toString(),
                        "email", "N/A",
                        "role", profile.getRole().name(),
                        "created_at", "N/A",
                        "email_confirmed", false
                    );
                    users.add(user);
                }
            }
            
            Map<String, Object> response = Map.of(
                "users", users,
                "total", users.size()
            );
            
            // Enviar notificación WebSocket sobre consulta de usuarios
            try {
                Map<String, Object> consultationNotification = Map.of(
                    "type", "users_consulted",
                    "total_users", users.size(),
                    "timestamp", System.currentTimeMillis()
                );
                webSocketNotificationService.notifyUsersConsulted(consultationNotification);
                logger.info("Notificación WebSocket enviada para consulta de usuarios: {} usuarios", users.size());
            } catch (Exception e) {
                logger.warn("Error al enviar notificación WebSocket para consulta de usuarios: {}", e.getMessage());
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to retrieve all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener usuarios: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable String id) {
        try {
            logger.info("Retrieving user with ID: {}", id);
            
            // Validate UUID format
            java.util.UUID userId;
            try {
                userId = java.util.UUID.fromString(id);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "ID de usuario inválido"));
            }
            
            // Get profile information from local database
            var profile = profileService.getProfileById(userId);
            if (profile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
            }
            
            // Get user information from Supabase
            var userInfo = supabaseAuthService.getUserById(id);
            
            Map<String, Object> response = Map.of(
                "id", id,
                "email", userInfo.getOrDefault("email", "N/A"),
                "role", profile.getRole().name(),
                "created_at", userInfo.getOrDefault("created_at", "N/A"),
                "email_confirmed", userInfo.get("email_confirmed_at") != null
            );
            
            // Enviar notificación WebSocket sobre consulta de usuario específico
            try {
                Map<String, Object> userConsultationNotification = Map.of(
                    "type", "user_consulted",
                    "user_id", id,
                    "email", userInfo.getOrDefault("email", "N/A"),
                    "role", profile.getRole().name(),
                    "timestamp", System.currentTimeMillis()
                );
                webSocketNotificationService.notifyUserConsulted(userConsultationNotification);
                logger.info("Notificación WebSocket enviada para consulta de usuario: {}", id);
            } catch (Exception e) {
                logger.warn("Error al enviar notificación WebSocket para consulta de usuario: {}", e.getMessage());
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to retrieve user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener usuario: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable String id, @Valid @RequestBody UserUpdateDto request) {
        try {
            logger.info("Updating user with ID: {}", id);
            
            // Validate UUID format
            java.util.UUID userId;
            try {
                userId = java.util.UUID.fromString(id);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "ID de usuario inválido"));
            }
            
            // Check if user exists
            var profile = profileService.getProfileById(userId);
            if (profile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
            }
            
            // Check if there are any updates
            if (!request.hasUpdates()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "No se proporcionaron campos para actualizar"));
            }
            
            // Update profile if role is provided
            if (request.getRole() != null) {
                try {
                    profile = profileService.updateProfile(userId, request.getRole());
                    logger.info("Profile updated for user {} with new role: {}", id, profile.getRole());
                } catch (Exception e) {
                    logger.error("Failed to update profile for user: {}", id, e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Error al actualizar perfil: " + e.getMessage()));
                }
            }
            
            // Update user info in Supabase if email or fullName is provided
            if (request.getEmail() != null || request.getFullName() != null) {
                try {
                    Map<String, Object> supabaseUpdates = new java.util.HashMap<>();
                    if (request.getEmail() != null) {
                        supabaseUpdates.put("email", request.getEmail());
                    }
                    if (request.getFullName() != null) {
                        Map<String, Object> userMetadata = new java.util.HashMap<>();
                        userMetadata.put("full_name", request.getFullName());
                        supabaseUpdates.put("user_metadata", userMetadata);
                    }
                    
                    supabaseAuthService.updateUser(id, supabaseUpdates);
                    logger.info("Supabase user info updated for user: {}", id);
                } catch (Exception e) {
                    logger.error("Failed to update Supabase user info for user: {}", id, e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Error al actualizar información en Supabase: " + e.getMessage()));
                }
            }
            
            // Get updated user info
            var userInfo = supabaseAuthService.getUserById(id);
            
            Map<String, Object> response = Map.of(
                "message", "Usuario actualizado exitosamente",
                "id", id,
                "email", userInfo.getOrDefault("email", "N/A"),
                "role", profile.getRole().name(),
                "full_name", userInfo.getOrDefault("full_name", "N/A"),
                "updated_at", System.currentTimeMillis()
            );
            
            // Enviar notificación WebSocket sobre usuario actualizado
            try {
                Map<String, Object> userUpdateNotification = Map.of(
                    "type", "user_updated",
                    "user_id", id,
                    "email", userInfo.getOrDefault("email", "N/A"),
                    "role", profile.getRole().name(),
                    "full_name", userInfo.getOrDefault("full_name", "N/A"),
                    "timestamp", System.currentTimeMillis()
                );
                webSocketNotificationService.notifyUserUpdated(userUpdateNotification);
                logger.info("Notificación WebSocket enviada para usuario actualizado: {}", id);
            } catch (Exception e) {
                logger.warn("Error al enviar notificación WebSocket para usuario actualizado: {}", e.getMessage());
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to update user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al actualizar usuario: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String id) {
        try {
            logger.info("Deleting user with ID: {}", id);
            
            // Validate UUID format
            java.util.UUID userId;
            try {
                userId = java.util.UUID.fromString(id);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "ID de usuario inválido"));
            }
            
            // Check if user exists
            var profile = profileService.getProfileById(userId);
            if (profile == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Usuario no encontrado"));
            }
            
            // Get user info before deletion for notification
            var userInfo = supabaseAuthService.getUserById(id);
            
            // Delete profile from local database
            try {
                profileService.deleteProfile(userId);
                logger.info("Profile deleted for user: {}", id);
            } catch (Exception e) {
                logger.error("Failed to delete profile for user: {}", id, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar perfil: " + e.getMessage()));
            }
            
            // Delete user from Supabase
            try {
                supabaseAuthService.deleteUser(id);
                logger.info("User deleted from Supabase: {}", id);
            } catch (Exception e) {
                logger.error("Failed to delete user from Supabase: {}", id, e);
                // Note: Profile is already deleted, this is a partial failure
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Usuario eliminado parcialmente. Perfil eliminado pero error en Supabase: " + e.getMessage()));
            }
            
            Map<String, Object> response = Map.of(
                "message", "Usuario eliminado exitosamente",
                "id", id,
                "deleted_at", System.currentTimeMillis()
            );
            
            // Enviar notificación WebSocket sobre usuario eliminado
            try {
                Map<String, Object> userDeleteNotification = Map.of(
                    "type", "user_deleted",
                    "user_id", id,
                    "email", userInfo.getOrDefault("email", "N/A"),
                    "role", profile.getRole().name(),
                    "timestamp", System.currentTimeMillis()
                );
                webSocketNotificationService.notifyUserDeleted(userDeleteNotification);
                logger.info("Notificación WebSocket enviada para usuario eliminado: {}", id);
            } catch (Exception e) {
                logger.warn("Error al enviar notificación WebSocket para usuario eliminado: {}", e.getMessage());
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to delete user with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al eliminar usuario: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "users"));
    }
}
