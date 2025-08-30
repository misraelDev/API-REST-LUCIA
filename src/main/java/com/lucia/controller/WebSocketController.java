package com.lucia.controller;

import com.lucia.service.WebSocketNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class WebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);
    
    private final WebSocketNotificationService webSocketNotificationService;

    public WebSocketController(WebSocketNotificationService webSocketNotificationService) {
        this.webSocketNotificationService = webSocketNotificationService;
    }

    /**
     * Endpoint para recibir mensajes del cliente y enviar respuesta
     */
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) {
        logger.info("Mensaje recibido del cliente: {}", message);
        return "Hola desde el servidor: " + message;
    }

    /**
     * Endpoint para mensajes privados
     */
    @MessageMapping("/private-message")
    @SendToUser("/queue/reply")
    public String privateMessage(String message) {
        logger.info("Mensaje privado recibido: {}", message);
        return "Respuesta privada: " + message;
    }

    /**
     * Endpoint de prueba para enviar notificaciones manualmente
     */
    @GetMapping("/api/ws/test-notification")
    @ResponseBody
    public Map<String, String> testNotification() {
        try {
            logger.info("Enviando notificación de prueba vía WebSocket");
            
            // Enviar heartbeat
            webSocketNotificationService.sendHeartbeat();
            
            // Enviar mensaje de prueba
            webSocketNotificationService.notifyNewRequest(null);
            
            return Map.of(
                "status", "success",
                "message", "Notificación de prueba enviada",
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación de prueba", e);
            return Map.of(
                "status", "error",
                "message", "Error: " + e.getMessage(),
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
        }
    }

    /**
     * Endpoint para verificar el estado de WebSocket
     */
    @GetMapping("/api/ws/status")
    @ResponseBody
    public Map<String, Object> getWebSocketStatus() {
        return Map.of(
            "status", "active",
            "endpoints", Map.of(
                "ws", "/ws",
                "topics", new String[]{
                    "/topic/requests/new",
                    "/topic/requests/updated",
                    "/topic/heartbeat",
                    "/topic/greetings"
                },
                "userTopics", new String[]{
                    "/user/{userId}/requests/new",
                    "/user/{userId}/requests/updated"
                }
            ),
            "timestamp", System.currentTimeMillis()
        );
    }
}
