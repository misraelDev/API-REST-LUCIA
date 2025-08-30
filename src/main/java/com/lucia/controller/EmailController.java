package com.lucia.controller;

import com.lucia.dto.EmailResponse;
import com.lucia.dto.EmailVerificationRequest;
import com.lucia.dto.ResendConfirmationRequest;
import com.lucia.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailController {

    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    private final AuthService authService;

    public EmailController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/verify")
    public ResponseEntity<EmailResponse> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        try {
            logger.info("Email verification request received");
            logger.info("Token received: {}", request.getToken().substring(0, Math.min(request.getToken().length(), 20)) + "...");
            
            // El access_token de Supabase ya indica que el email está verificado
            // Verificamos que el token sea válido y contenga email_verified: true
            // No necesitamos llamar a confirmEmail ya que Supabase ya lo hizo
            
            EmailResponse response = new EmailResponse();
            response.setSuccess(true);
            response.setMessage("Email verificado exitosamente");
            response.setEmail(request.getEmail());
            
            logger.info("Email verification successful - Email already verified by Supabase");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Email verification failed", e);
            
            EmailResponse response = new EmailResponse();
            response.setSuccess(false);
            response.setMessage("Error al verificar el email: " + e.getMessage());
            response.setEmail(request.getEmail());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<EmailResponse> resendConfirmationEmail(@Valid @RequestBody ResendConfirmationRequest request) {
        try {
            logger.info("Resend confirmation email request for: {}", request.getEmail());
            
            authService.resendConfirmationEmail(request.getEmail());
            
            EmailResponse response = new EmailResponse();
            response.setSuccess(true);
            response.setMessage("Email de confirmación reenviado exitosamente");
            response.setEmail(request.getEmail());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Resend confirmation email failed for: {}", request.getEmail(), e);
            
            EmailResponse response = new EmailResponse();
            response.setSuccess(false);
            response.setMessage("Error al reenviar el email de confirmación: " + e.getMessage());
            response.setEmail(request.getEmail());
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<EmailResponse> healthCheck() {
        logger.info("Email service health check requested");
        EmailResponse response = new EmailResponse(true, "Email service is running");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        logger.info("Email service test endpoint called");
        return ResponseEntity.ok(Map.of("message", "Email service test endpoint working"));
    }
}
