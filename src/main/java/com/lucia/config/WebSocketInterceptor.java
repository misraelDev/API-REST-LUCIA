package com.lucia.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class WebSocketInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null) {
            String sessionId = accessor.getSessionId();
            String destination = accessor.getDestination();
            String command = accessor.getCommand() != null ? accessor.getCommand().name() : "UNKNOWN";
            
            logger.info("WebSocket {} - Session: {}, Destination: {}", command, sessionId, destination);
            
            // Log específico para conexiones
            if (accessor.getCommand() != null) {
                switch (accessor.getCommand()) {
                    case CONNECT:
                        logger.info("Nueva conexión WebSocket establecida - Session: {}", sessionId);
                        break;
                    case DISCONNECT:
                        logger.info("Conexión WebSocket cerrada - Session: {}", sessionId);
                        break;
                    case SUBSCRIBE:
                        logger.info("Nueva suscripción - Session: {}, Topic: {}", sessionId, destination);
                        break;
                    default:
                        logger.debug("Comando WebSocket: {} - Session: {}", command, sessionId);
                        break;
                }
            }
        }
        
        return message;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        if (ex != null) {
            logger.error("Error en WebSocket después del envío", ex);
        }
    }
}
