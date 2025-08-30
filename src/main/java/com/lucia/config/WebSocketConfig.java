package com.lucia.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        logger.info("Configurando Message Broker para WebSocket");
        
        // Habilitar broker simple para topics
        config.enableSimpleBroker("/topic", "/queue");
        
        // Prefijo para mensajes de aplicación
        config.setApplicationDestinationPrefixes("/app");
        
        // Configurar prefijos de usuario para mensajes privados
        config.setUserDestinationPrefix("/user");
        
        // Configurar heartbeat para mantener conexiones activas
        config.setPreservePublishOrder(true);
        
        logger.info("Message Broker configurado con topics: /topic, /queue");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        logger.info("Registrando endpoints de WebSocket");
        
        // Endpoint principal con SockJS
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setHeartbeatTime(25000)
                .setDisconnectDelay(5000);
        
        // Endpoint nativo sin SockJS para clientes que no lo necesiten
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
        
        logger.info("Endpoints de WebSocket registrados: /ws (con y sin SockJS)");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        logger.info("Configurando transporte de WebSocket");
        
        // Configurar timeouts
        registration.setSendTimeLimit(20000);
        registration.setSendBufferSizeLimit(512 * 1024);
        registration.setMessageSizeLimit(128 * 1024);
        
        // Configurar heartbeat
        registration.setTimeToFirstMessage(30000);
        
        logger.info("Transporte de WebSocket configurado con timeouts y límites");
    }
}
