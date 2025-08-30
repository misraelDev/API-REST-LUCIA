package com.lucia.service;

import com.lucia.entity.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketNotificationService.class);
    
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Notifica a todos los clientes sobre una nueva solicitud
     */
    public void notifyNewRequest(Request request) {
        try {
            logger.info("Enviando notificación WebSocket para nueva solicitud ID: {}", request.getId());
            
            // Notificar a todos los clientes suscritos al topic general
            messagingTemplate.convertAndSend("/topic/requests/new", request);
            
            // Notificar específicamente al seller si tiene sellerId
            if (request.getSellerId() != null) {
                String sellerTopic = "/topic/seller/" + request.getSellerId() + "/requests/new";
                messagingTemplate.convertAndSend(sellerTopic, request);
                logger.info("Notificación enviada al seller {} en topic: {}", request.getSellerId(), sellerTopic);
            }
            
            logger.info("Notificación WebSocket enviada exitosamente para solicitud ID: {}", request.getId());
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket para solicitud ID: {}", request.getId(), e);
        }
    }

    /**
     * Notifica a todos los clientes sobre una solicitud actualizada
     */
    public void notifyRequestUpdated(Request request) {
        try {
            logger.info("Enviando notificación WebSocket para solicitud actualizada ID: {}", request.getId());
            
            // Notificar a todos los clientes suscritos al topic general
            messagingTemplate.convertAndSend("/topic/requests/updated", request);
            
            // Notificar específicamente al seller si tiene sellerId
            if (request.getSellerId() != null) {
                String sellerTopic = "/topic/seller/" + request.getSellerId() + "/requests/updated";
                messagingTemplate.convertAndSend(sellerTopic, request);
                logger.info("Notificación enviada al seller {} en topic: {}", request.getSellerId(), sellerTopic);
            }
            
            logger.info("Notificación WebSocket enviada exitosamente para solicitud actualizada ID: {}", request.getId());
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket para solicitud actualizada ID: {}", request.getId(), e);
        }
    }

    /**
     * Notifica a un usuario específico sobre sus solicitudes
     */
    public void notifyUserRequest(String userId, Request request, String action) {
        try {
            String userTopic = "/user/" + userId + "/requests/" + action;
            logger.info("Enviando notificación personalizada a usuario {} en topic: {}", userId, userTopic);
            
            messagingTemplate.convertAndSendToUser(userId, "/requests/" + action, request);
            
            logger.info("Notificación personalizada enviada exitosamente a usuario: {}", userId);
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación personalizada a usuario: {}", userId, e);
        }
    }

    /**
     * Envía un heartbeat para mantener las conexiones activas
     */
    public void sendHeartbeat() {
        try {
            messagingTemplate.convertAndSend("/topic/heartbeat", "ping");
            logger.debug("Heartbeat enviado a todos los clientes conectados");
        } catch (Exception e) {
            logger.debug("Error al enviar heartbeat: {}", e.getMessage());
        }
    }

    /**
     * Notifica a todos los clientes que se consultaron las estadísticas
     */
    public void notifyStatsRequested(java.util.Map<String, Object> stats) {
        try {
            logger.info("Enviando notificación WebSocket de consulta de estadísticas");
            
            // Notificar a todos los clientes suscritos al topic de estadísticas
            messagingTemplate.convertAndSend("/topic/requests/stats/consulted", stats);
            
            logger.info("Notificación WebSocket de estadísticas enviada exitosamente");
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación WebSocket de estadísticas", e);
        }
    }
}
