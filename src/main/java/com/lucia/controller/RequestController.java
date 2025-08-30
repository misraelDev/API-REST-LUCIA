package com.lucia.controller;

import com.lucia.dto.RequestCreateDto;
import com.lucia.entity.Request;
import com.lucia.service.RequestService;
import com.lucia.service.EmailService;
import com.lucia.service.ReferralService;
import com.lucia.service.WebSocketNotificationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "*")
public class RequestController {

    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    private final RequestService requestService;
    private final EmailService emailService;
    private final ReferralService referralService;
    private final WebSocketNotificationService webSocketNotificationService;

    public RequestController(RequestService requestService, EmailService emailService, ReferralService referralService, WebSocketNotificationService webSocketNotificationService) {
        this.requestService = requestService;
        this.emailService = emailService;
        this.referralService = referralService;
        this.webSocketNotificationService = webSocketNotificationService;
    }

    /**
     * Crea una nueva solicitud de contacto
     */
    @PostMapping
    public ResponseEntity<Request> createRequest(@Valid @RequestBody RequestCreateDto requestDto) {
        try {
            logger.info("Creating contact request for: {}", requestDto.getEmail());
            
            Request createdRequest = requestService.createRequest(requestDto);
            
            // Notificar a todos los clientes conectados vía WebSocket
            webSocketNotificationService.notifyNewRequest(createdRequest);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
            
        } catch (Exception e) {
            logger.error("Failed to create contact request for: {}", requestDto.getEmail(), e);
            throw e;
        }
    }

    /**
     * Obtiene todas las solicitudes
     */
    @GetMapping
    public ResponseEntity<List<Request>> getAllRequests() {
        try {
            List<Request> requests = requestService.getAllRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            logger.error("Failed to get all requests", e);
            throw e;
        }
    }

    /**
     * Obtiene una solicitud por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Request> getRequestById(@PathVariable Long id) {
        try {
            var request = requestService.getRequestById(id);
            if (request.isPresent()) {
                return ResponseEntity.ok(request.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Failed to get request with ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Obtiene solicitudes por código de referido
     */
    @GetMapping("/referral/{referralCode}")
    public ResponseEntity<List<Request>> getRequestsByReferralCode(@PathVariable String referralCode) {
        try {
            List<Request> requests = requestService.getRequestsByReferralCode(referralCode);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            logger.error("Failed to get requests by referral code: {}", referralCode, e);
            throw e;
        }
    }

    /**
     * Obtiene solicitudes por email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<List<Request>> getRequestsByEmail(@PathVariable String email) {
        try {
            List<Request> requests = requestService.getRequestsByEmail(email);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            logger.error("Failed to get requests by email: {}", email, e);
            throw e;
        }
    }

    /**
     * Obtiene solicitudes por estado
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Request>> getRequestsByStatus(@PathVariable String status) {
        try {
            Request.RequestStatus requestStatus = Request.RequestStatus.valueOf(status.toUpperCase());
            List<Request> requests = requestService.getRequestsByStatus(requestStatus);
            return ResponseEntity.ok(requests);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Failed to get requests by status: {}", status, e);
            throw e;
        }
    }

    /**
     * Actualiza el estado de una solicitud
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Request> updateRequestStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, String> statusUpdate) {
        try {
            String newStatus = statusUpdate.get("status");
            if (newStatus == null) {
                return ResponseEntity.badRequest().build();
            }

            Request.RequestStatus requestStatus = Request.RequestStatus.valueOf(newStatus.toUpperCase());
            Request updatedRequest = requestService.updateRequestStatus(id, requestStatus);
            
            // Notificar a todos los clientes conectados vía WebSocket
            webSocketNotificationService.notifyRequestUpdated(updatedRequest);
            
            return ResponseEntity.ok(updatedRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Failed to update request status for ID: {}", id, e);
            throw e;
        }
    }



    /**
     * Obtiene estadísticas de solicitudes
     */
    @GetMapping("/stats")
    public ResponseEntity<RequestService.RequestStats> getRequestStats() {
        try {
            RequestService.RequestStats stats = requestService.getRequestStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Failed to get request stats", e);
            throw e;
        }
    }

    /**
     * Health check del servicio de solicitudes
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "requests"));
    }
    
    /**
     * Envía un email de contacto usando Resend
     */
    @PostMapping("/contact-email")
    public ResponseEntity<Map<String, Object>> sendContactEmail(@RequestBody Map<String, String> contactData) {
        try {
            logger.info("Enviando email de contacto para: {}", contactData.get("email"));
            
            // Validación básica
            String name = contactData.get("name");
            String email = contactData.get("email");
            String message = contactData.get("message");
            
            if (name == null || name.trim().isEmpty() || 
                email == null || email.trim().isEmpty() || 
                message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Faltan campos requeridos: name, email, message"));
            }
            
            String phone = contactData.get("phone");
            String need = contactData.get("need");
            String referralCode = contactData.get("referralCode");
            
            // 1. ENVIAR EMAIL
            boolean emailSent = emailService.sendContactEmail(name, email, phone, need, message);

            // 2. GUARDAR EN BASE DE DATOS
            RequestCreateDto requestDto = new RequestCreateDto();
            requestDto.setName(name);
            requestDto.setEmail(email);
            requestDto.setPhone(phone);
            requestDto.setNeed(need);
            requestDto.setMessage(message);
            requestDto.setReferralCode(referralCode);

            Request savedRequest = requestService.createRequest(requestDto);
            
            // Notificar a todos los clientes conectados vía WebSocket
            webSocketNotificationService.notifyNewRequest(savedRequest);

            // 3. VALIDAR REFERRAL CODE SI SE PROPORCIONA
            if (referralCode != null && !referralCode.trim().isEmpty()) {
                // Buscar el sellerId del código de referido
                try {
                    String sellerId = referralService.getSellerIdFromReferralCode(referralCode);
                    if (sellerId != null) {
                        // Actualizar la solicitud con el sellerId
                        savedRequest.setSellerId(sellerId);
                        requestService.updateRequest(savedRequest);
                        logger.info("Referral code validated and sellerId set: {} for request: {}", referralCode, savedRequest.getId());
                    } else {
                        logger.error("Invalid referral code provided: {}. Rejecting request.", referralCode);
                        return ResponseEntity.badRequest()
                            .body(Map.of("error", "Código de referido inválido: " + referralCode));
                    }
                } catch (Exception e) {
                    logger.error("Error validating referral code: {}. Error: {}", referralCode, e.getMessage());
                    return ResponseEntity.badRequest()
                        .body(Map.of("error", "Error al validar el código de referido: " + referralCode));
                }
            }

            if (emailSent && savedRequest != null) {
                logger.info("Email enviado y solicitud guardada exitosamente para: {}", email);
                
                // Construir respuesta base
                Map<String, Object> response = Map.of(
                    "message", "Email enviado y solicitud guardada correctamente",
                    "email", email,
                    "requestId", savedRequest.getId()
                );
                
                // Agregar información del referido si se proporcionó
                if (referralCode != null && !referralCode.trim().isEmpty()) {
                    response = Map.of(
                        "message", "Email enviado y solicitud guardada correctamente",
                        "email", email,
                        "requestId", savedRequest.getId(),
                        "referralCode", referralCode
                    );
                }
                
                return ResponseEntity.ok(response);
            } else {
                logger.error("Error al enviar email o guardar solicitud para: {}", email);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al enviar el email o guardar la solicitud"));
            }
            
        } catch (Exception e) {
            logger.error("Error inesperado al enviar email de contacto", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }
}
