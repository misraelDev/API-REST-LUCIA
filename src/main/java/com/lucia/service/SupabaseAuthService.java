package com.lucia.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucia.config.SupabaseConfig;
import com.lucia.dto.AuthResponse;
import com.lucia.dto.LoginRequest;
import com.lucia.dto.RegisterRequest;
import com.lucia.entity.Profile;
import com.lucia.exception.AuthenticationException;
import com.lucia.repository.ProfileRepository;
import com.lucia.service.ReferralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

@Service
public class SupabaseAuthService {

    private static final Logger logger = LoggerFactory.getLogger(SupabaseAuthService.class);

    private final WebClient webClient;
    private final WebClient adminClient;
    private final ObjectMapper objectMapper;
    private final ProfileRepository profileRepository;
    private final ReferralService referralService;

    public SupabaseAuthService(SupabaseConfig supabaseConfig, ObjectMapper objectMapper, ProfileRepository profileRepository, ReferralService referralService) {
        this.webClient = WebClient.builder()
                .baseUrl(supabaseConfig.getSupabaseUrl() + "/auth/v1")
                .defaultHeader("apikey", supabaseConfig.getAnonKey())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Authorization", "Bearer " + supabaseConfig.getAnonKey())
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
        this.adminClient = WebClient.builder()
                .baseUrl(supabaseConfig.getSupabaseUrl() + "/auth/v1/admin")
                .defaultHeader("apikey", supabaseConfig.getServiceRoleKey())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Authorization", "Bearer " + supabaseConfig.getServiceRoleKey())
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
        this.objectMapper = objectMapper;
        this.profileRepository = profileRepository;
        this.referralService = referralService;
    }

    public boolean userExistsByEmail(String email) {
        try {
            String json = adminClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/users").queryParam("email", email).build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("Admin lookup response for {}: {}", email, json);
            
            JsonNode node = objectMapper.readTree(json);
            // Supabase Admin devuelve {"users":[...], "aud":"..."}
            if (node.has("users") && node.get("users").isArray()) {
                JsonNode users = node.get("users");
                boolean matched = false;
                for (JsonNode u : users) {
                    String primaryEmail = u.path("email").asText(null);
                    String metadataEmail = u.path("user_metadata").path("email").asText(null);
                    boolean equalsPrimary = primaryEmail != null && email.equalsIgnoreCase(primaryEmail);
                    boolean equalsMetadata = metadataEmail != null && email.equalsIgnoreCase(metadataEmail);
                    if (equalsPrimary || equalsMetadata) {
                        matched = true;
                        break;
                    }
                }
                logger.info("Admin lookup: users array with {} items, exactMatch: {}", users.size(), matched);
                return matched;
            }
            // Fallback: si no tiene estructura esperada, asumir que no existe
            logger.info("Admin lookup: unexpected structure, assuming user doesn't exist");
            return false;
        } catch (WebClientResponseException e) {
            logger.error("Supabase admin user lookup error: {} - Status: {}", e.getResponseBodyAsString(), e.getStatusCode());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during admin user lookup", e);
            return false;
        }
    }

    public AuthResponse signUp(RegisterRequest request) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("email", request.getEmail());
            body.put("password", request.getPassword());

            // Add user metadata if full name is provided
            if (request.getFullName() != null) {
                Map<String, Object> userMetadata = new HashMap<>();
                userMetadata.put("full_name", request.getFullName());
                body.put("data", userMetadata);
            }

            logger.info("Attempting to sign up user with email: {}", request.getEmail());

            String response = webClient.post()
                    .uri("/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("Signup response received: {}", response);
            return parseAuthResponse(response);

        } catch (WebClientResponseException e) {
            logger.error("Supabase signup error: {} - Status: {}", e.getResponseBodyAsString(), e.getStatusCode());
            throw new AuthenticationException("Error al registrar usuario: " + parseError(e.getResponseBodyAsString()));
        } catch (Exception e) {
            logger.error("Unexpected error during signup", e);
            throw new AuthenticationException("Error interno del servidor");
        }
    }

    public AuthResponse signIn(LoginRequest request) {
        try {
            Map<String, String> body = new HashMap<>();
            body.put("email", request.getEmail());
            body.put("password", request.getPassword());

            logger.info("Attempting to sign in user with email: {}", request.getEmail());

            String response = webClient.post()
                    .uri("/token?grant_type=password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("Signin response received: {}", response);
            AuthResponse authResponse = parseAuthResponse(response);
            
            // Obtener el rol del usuario desde profiles
            if (authResponse.getUserId() != null) {
                try {
                    java.util.UUID userId = java.util.UUID.fromString(authResponse.getUserId());
                    logger.info("Looking for profile with userId: {}", userId);
                    Profile profile = profileRepository.findById(userId).orElse(null);
                    if (profile != null) {
                        authResponse.setRole(profile.getRole().name());
                        logger.info("User role retrieved: {}", profile.getRole());
                        
                        // Si el usuario es seller, obtener su referral_code
                        if (profile.getRole() == Profile.Role.seller) {
                            try {
                                String referralCode = referralService.getReferralBySellerId(authResponse.getUserId()).getReferralCode();
                                authResponse.setReferralCode(referralCode);
                                logger.info("Referral code retrieved for seller: {}", referralCode);
                            } catch (Exception e) {
                                logger.warn("No referral code found for seller: {}", authResponse.getUserId());
                                // No lanzar excepción, simplemente no incluir referral_code
                            }
                        }
                    } else {
                        logger.warn("No profile found for userId: {}", userId);
                    }
                } catch (IllegalArgumentException e) {
                    logger.warn("Invalid UUID format for userId: {}", authResponse.getUserId());
                }
            }
            
            return authResponse;

        } catch (WebClientResponseException e) {
            logger.error("Supabase signin error: {} - Status: {}", e.getResponseBodyAsString(), e.getStatusCode());
            
            // Check for specific error types
            String errorResponse = e.getResponseBodyAsString();
            if (errorResponse.contains("email_not_confirmed")) {
                throw new AuthenticationException("El correo electrónico no está confirmado. Por favor, revisa tu bandeja de entrada y confirma tu email antes de iniciar sesión.");
            } else if (errorResponse.contains("Invalid login credentials")) {
                throw new AuthenticationException("Credenciales inválidas");
            } else {
                throw new AuthenticationException("Error al iniciar sesión: " + parseError(errorResponse));
            }
        } catch (Exception e) {
            logger.error("Unexpected error during signin", e);
            throw new AuthenticationException("Error interno del servidor");
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        try {
            Map<String, String> body = new HashMap<>();
            body.put("refresh_token", refreshToken);

            String response = webClient.post()
                    .uri("/token?grant_type=refresh_token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseAuthResponse(response);

        } catch (WebClientResponseException e) {
            logger.error("Supabase refresh token error: {} - Status: {}", e.getResponseBodyAsString(), e.getStatusCode());
            throw new AuthenticationException("Token de actualización inválido");
        } catch (Exception e) {
            logger.error("Unexpected error during token refresh", e);
            throw new AuthenticationException("Error interno del servidor");
        }
    }

    public void signOut(String accessToken) {
        try {
            webClient.post()
                    .uri("/logout")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (WebClientResponseException e) {
            logger.error("Supabase signout error: {} - Status: {}", e.getResponseBodyAsString(), e.getStatusCode());
        } catch (Exception e) {
            logger.error("Unexpected error during signout", e);
        }
    }

    public AuthResponse confirmEmail(String token, String email) {
        try {
            logger.info("Attempting to confirm email with token: {} and email: {}", 
                token.substring(0, Math.min(token.length(), 20)) + "...", email);

            String response = webClient.post()
                    .uri("/verify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("token", token, "type", "signup", "email", email))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("Email confirmation response received: {}", response);
            return parseAuthResponse(response);

        } catch (WebClientResponseException e) {
            logger.error("Supabase email confirmation error: {} - Status: {}", e.getResponseBodyAsString(), e.getStatusCode());
            throw new AuthenticationException("Error al confirmar el email: " + parseError(e.getResponseBodyAsString()));
        } catch (Exception e) {
            logger.error("Unexpected error during email confirmation", e);
            throw new AuthenticationException("Error interno del servidor");
        }
    }

    public void resendConfirmationEmail(String email) {
        try {
            logger.info("Attempting to resend confirmation email to: {}", email);

            String response = webClient.post()
                    .uri("/resend")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("email", email, "type", "signup"))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("Resend confirmation email response received: {}", response);

        } catch (WebClientResponseException e) {
            logger.error("Supabase resend confirmation email error: {} - Status: {}", e.getResponseBodyAsString(), e.getStatusCode());
            throw new AuthenticationException("Error al reenviar el email de confirmación: " + parseError(e.getResponseBodyAsString()));
        } catch (Exception e) {
            logger.error("Unexpected error during resend confirmation email", e);
            throw new AuthenticationException("Error interno del servidor");
        }
    }

    public Map<String, Object> getUserById(String userId) {
        try {
            logger.info("Retrieving user information for ID: {}", userId);

            String response = adminClient.get()
                    .uri("/users/" + userId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("User info response received: {}", response);
            
            JsonNode node = objectMapper.readTree(response);
            Map<String, Object> userInfo = new HashMap<>();
            
            if (node.has("id")) {
                userInfo.put("id", node.get("id").asText());
            }
            if (node.has("email")) {
                userInfo.put("email", node.get("email").asText());
            }
            if (node.has("created_at")) {
                userInfo.put("created_at", node.get("created_at").asText());
            }
            if (node.has("email_confirmed_at")) {
                userInfo.put("email_confirmed_at", node.get("email_confirmed_at").asText());
            }
            if (node.has("user_metadata")) {
                JsonNode metadata = node.get("user_metadata");
                if (metadata.has("full_name")) {
                    userInfo.put("full_name", metadata.get("full_name").asText());
                }
            }
            
            return userInfo;

        } catch (WebClientResponseException e) {
            logger.error("Supabase get user by ID error: {} - Status: {}", e.getResponseBodyAsString(), e.getStatusCode());
            throw new AuthenticationException("Error al obtener información del usuario: " + parseError(e.getResponseBodyAsString()));
        } catch (Exception e) {
            logger.error("Unexpected error during get user by ID", e);
            throw new AuthenticationException("Error interno del servidor");
        }
    }

    public boolean updateUser(String userId, Map<String, Object> updates) {
        try {
            logger.info("Updating user information for ID: {}", userId);

            String response = adminClient.put()
                    .uri("/users/" + userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updates)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("User update response received: {}", response);
            return true;

        } catch (WebClientResponseException e) {
            logger.error("Supabase update user error: {} - Status: {}", e.getResponseBodyAsString(), e.getStatusCode());
            throw new AuthenticationException("Error al actualizar usuario: " + parseError(e.getResponseBodyAsString()));
        } catch (Exception e) {
            logger.error("Unexpected error during user update", e);
            throw new AuthenticationException("Error interno del servidor");
        }
    }

    public boolean deleteUser(String userId) {
        try {
            logger.info("Deleting user with ID: {}", userId);

            adminClient.delete()
                    .uri("/users/" + userId)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("User deleted successfully: {}", userId);
            return true;

        } catch (WebClientResponseException e) {
            logger.error("Supabase delete user error: {} - Status: {}", e.getResponseBodyAsString(), e.getStatusCode());
            throw new AuthenticationException("Error al eliminar usuario: " + parseError(e.getResponseBodyAsString()));
        } catch (Exception e) {
            logger.error("Unexpected error during user deletion", e);
            throw new AuthenticationException("Error interno del servidor");
        }
    }

    public AuthResponse verifyUserToken(String accessToken) {
        try {
            logger.info("Verifying user token...");

            String response = webClient.get()
                    .uri("/user")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("User verification response received: {}", response);
            return parseAuthResponse(response);

        } catch (WebClientResponseException e) {
            logger.error("Supabase user verification error: {} - Status: {}", e.getResponseBodyAsString(), e.getStatusCode());
            throw new AuthenticationException("Error al verificar el usuario: " + parseError(e.getResponseBodyAsString()));
        } catch (Exception e) {
            logger.error("Unexpected error during user verification", e);
            throw new AuthenticationException("Error interno del servidor");
        }
    }

    private AuthResponse parseAuthResponse(String jsonResponse) {
        try {
            JsonNode node = objectMapper.readTree(jsonResponse);

            AuthResponse authResponse = new AuthResponse();
            
            // Handle user registration response (no session/tokens)
            if (node.has("id")) {
                authResponse.setUserId(node.get("id").asText());
            }
            if (node.has("email")) {
                authResponse.setEmail(node.get("email").asText());
            }
            
            // Handle user object in session response
            if (node.has("user")) {
                JsonNode userNode = node.get("user");
                if (userNode.has("id")) {
                    authResponse.setUserId(userNode.get("id").asText());
                }
                if (userNode.has("email")) {
                    authResponse.setEmail(userNode.get("email").asText());
                }
            }
            
            // Handle session object (for login responses)
            if (node.has("session")) {
                JsonNode sessionNode = node.get("session");
                if (sessionNode.has("access_token")) {
                    authResponse.setAccessToken(sessionNode.get("access_token").asText());
                }
                if (sessionNode.has("refresh_token")) {
                    authResponse.setRefreshToken(sessionNode.get("refresh_token").asText());
                }
                if (sessionNode.has("expires_at")) {
                    authResponse.setExpiresIn(sessionNode.get("expires_at").asLong());
                }
            } else {
                // Direct token response (legacy format)
                if (node.has("access_token")) {
                    authResponse.setAccessToken(node.get("access_token").asText());
                }
                if (node.has("refresh_token")) {
                    authResponse.setRefreshToken(node.get("refresh_token").asText());
                }
                if (node.has("expires_in")) {
                    authResponse.setExpiresIn(node.get("expires_in").asLong());
                }
            }
            
            authResponse.setTokenType("Bearer");

            return authResponse;

        } catch (Exception e) {
            logger.error("Error parsing auth response: {}", jsonResponse, e);
            throw new AuthenticationException("Error procesando respuesta de autenticación");
        }
    }

    private String parseError(String errorResponse) {
        try {
            JsonNode node = objectMapper.readTree(errorResponse);
            if (node.has("msg")) {
                return node.get("msg").asText();
            } else if (node.has("error_description")) {
                return node.get("error_description").asText();
            } else if (node.has("error")) {
                return node.get("error").asText();
            } else if (node.has("message")) {
                return node.get("message").asText();
            }
            return "Error desconocido";
        } catch (Exception e) {
            return "Error desconocido";
        }
    }
}


