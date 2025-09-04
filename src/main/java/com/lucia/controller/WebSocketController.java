package com.lucia.controller;

import com.lucia.entity.Contact;
import com.lucia.service.WebSocketNotificationService;
import com.lucia.service.StatsService;
import com.lucia.dto.DashboardStatsDto;
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
    private final StatsService statsService;

    public WebSocketController(WebSocketNotificationService webSocketNotificationService, StatsService statsService) {
        this.webSocketNotificationService = webSocketNotificationService;
        this.statsService = statsService;
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
     * Endpoint de prueba para enviar notificaciones de appointments manualmente
     */
    @GetMapping("/api/ws/test-appointment-notification")
    @ResponseBody
    public Map<String, String> testAppointmentNotification() {
        try {
            logger.info("Enviando notificación de prueba de appointment vía WebSocket");
            
            // Crear una cita de prueba
            com.lucia.entity.Appointment testAppointment = new com.lucia.entity.Appointment();
            testAppointment.setId(999L);
            testAppointment.setSummary("Cita de prueba");
            testAppointment.setStatus(com.lucia.entity.Appointment.AppointmentStatus.CONFIRMED);
            
            // Enviar heartbeat
            webSocketNotificationService.sendHeartbeat();
            
            // Enviar notificación de nueva cita
            webSocketNotificationService.notifyNewAppointment(testAppointment);
            
            return Map.of(
                "status", "success",
                "message", "Notificación de prueba de appointment enviada",
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación de prueba de appointment", e);
            return Map.of(
                "status", "error",
                "message", "Error: " + e.getMessage(),
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
        }
    }

    /**
     * Endpoint de prueba para enviar notificaciones de contactos manualmente
     */
    @GetMapping("/api/ws/test-contact-notification")
    @ResponseBody
    public Map<String, String> testContactNotification() {
        try {
            logger.info("Enviando notificación de prueba de contacto vía WebSocket");
            
            // Crear un contacto de prueba
            com.lucia.entity.Contact testContact = new com.lucia.entity.Contact();
            testContact.setId(999L);
            testContact.setName("Contacto de prueba");
            testContact.setEmail("test@example.com");
            testContact.setPhoneNumber("+1234567890");
            
            // Enviar heartbeat
            webSocketNotificationService.sendHeartbeat();
            
            // Enviar notificación de nuevo contacto
            webSocketNotificationService.notifyNewContact(testContact);
            
            return Map.of(
                "status", "success",
                "message", "Notificación de prueba de contacto enviada",
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación de prueba de contacto", e);
            return Map.of(
                "status", "error",
                "message", "Error: " + e.getMessage(),
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
        }
    }

    /**
     * Endpoint de prueba para enviar notificaciones de llamadas manualmente
     */
    @GetMapping("/api/ws/test-call-notification")
    @ResponseBody
    public Map<String, String> testCallNotification() {
        try {
            logger.info("Enviando notificación de prueba de llamada vía WebSocket");
            
            // Crear una llamada de prueba
            com.lucia.entity.Call testCall = new com.lucia.entity.Call();
            testCall.setId(999L);
            testCall.setDate(java.time.LocalDateTime.now());
            testCall.setDuration(300); // 5 minutos
            testCall.setMotive("Llamada de prueba");
            Contact testContact = new Contact();
            testContact.setId(1L);
            testCall.setContact(testContact);
            testCall.setSummary("Resumen de llamada de prueba");
            testCall.setIntent("Consulta");
            testCall.setMessages("{\"transcript\": \"Hola, esta es una llamada de prueba\"}");
            testCall.setAudioCombined("https://example.com/audio/combined.mp3");
            testCall.setAudioAssistant("https://example.com/audio/assistant.mp3");
            testCall.setAudioCustomer("https://example.com/audio/customer.mp3");
            
            // Enviar heartbeat
            webSocketNotificationService.sendHeartbeat();
            
            // Enviar notificación de nueva llamada
            webSocketNotificationService.notifyNewCall(testCall);
            
            return Map.of(
                "status", "success",
                "message", "Notificación de prueba de llamada enviada",
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación de prueba de llamada", e);
            return Map.of(
                "status", "error",
                "message", "Error: " + e.getMessage(),
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
        }
    }

    /**
     * Endpoint de prueba para enviar notificaciones del dashboard manualmente
     */
    @GetMapping("/api/ws/test-dashboard-notification")
    @ResponseBody
    public Map<String, String> testDashboardNotification() {
        try {
            logger.info("Enviando notificación de prueba del dashboard vía WebSocket");
            
            // Obtener datos reales del dashboard
            long totalCalls = statsService.getTotalCalls();
            long totalContacts = statsService.getTotalContacts();
            long totalAppointments = statsService.getTotalAppointments();
            double averageCallDuration = statsService.getAverageCallDuration();
            
            java.util.List<java.util.Map<String, Object>> historicalData = statsService.getHistoricalData("1 año");
            java.util.List<java.util.Map<String, Object>> topConversations = statsService.getTopConversations();
            java.util.List<java.util.Map<String, Object>> frequentMotives = statsService.getTopFrequentMotives();
            java.util.List<java.util.Map<String, Object>> recentContacts = statsService.getRecentContacts();
            java.util.Map<String, Object> summary = statsService.getCallsAndAppointmentsSummary();
            
            // Crear DTO del dashboard
            DashboardStatsDto dashboardStats = new DashboardStatsDto(
                totalCalls,
                totalContacts,
                totalAppointments,
                averageCallDuration,
                historicalData,
                topConversations,
                frequentMotives,
                recentContacts,
                summary
            );
            
            // Enviar heartbeat
            webSocketNotificationService.sendHeartbeat();
            
            // Enviar notificación del dashboard
            webSocketNotificationService.notifyDashboardStatsConsulted(dashboardStats);
            
            return Map.of(
                "status", "success",
                "message", "Notificación de prueba del dashboard enviada",
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación de prueba del dashboard", e);
            return Map.of(
                "status", "error",
                "message", "Error: " + e.getMessage(),
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
        }
    }

    /**
     * Endpoint de prueba para enviar notificaciones de usuarios manualmente
     */
    @GetMapping("/api/ws/test-user-notification")
    @ResponseBody
    public Map<String, String> testUserNotification() {
        try {
            logger.info("Enviando notificación de prueba de usuario vía WebSocket");
            
            // Crear un usuario de prueba
            Map<String, Object> testUser = Map.of(
                "type", "user_created",
                "user_id", "test-uuid-123",
                "email", "test@example.com",
                "role", "user",
                "timestamp", System.currentTimeMillis()
            );
            
            // Enviar heartbeat
            webSocketNotificationService.sendHeartbeat();
            
            // Enviar notificación de nuevo usuario
            webSocketNotificationService.notifyNewUser(testUser);
            
            return Map.of(
                "status", "success",
                "message", "Notificación de prueba de usuario enviada",
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación de prueba de usuario", e);
            return Map.of(
                "status", "error",
                "message", "Error: " + e.getMessage(),
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
        }
    }

    /**
     * Endpoint de prueba para enviar notificaciones de actualización de usuarios manualmente
     */
    @GetMapping("/api/ws/test-user-update-notification")
    @ResponseBody
    public Map<String, String> testUserUpdateNotification() {
        try {
            logger.info("Enviando notificación de prueba de actualización de usuario vía WebSocket");
            
            // Crear un usuario de prueba actualizado
            Map<String, Object> testUserUpdate = Map.of(
                "type", "user_updated",
                "user_id", "test-uuid-123",
                "email", "updated@example.com",
                "role", "seller",
                "full_name", "Usuario Actualizado",
                "timestamp", System.currentTimeMillis()
            );
            
            // Enviar heartbeat
            webSocketNotificationService.sendHeartbeat();
            
            // Enviar notificación de usuario actualizado
            webSocketNotificationService.notifyUserUpdated(testUserUpdate);
            
            return Map.of(
                "status", "success",
                "message", "Notificación de prueba de actualización de usuario enviada",
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación de prueba de actualización de usuario", e);
            return Map.of(
                "status", "error",
                "message", "Error: " + e.getMessage(),
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
        }
    }

    /**
     * Endpoint de prueba para enviar notificaciones de eliminación de usuarios manualmente
     */
    @GetMapping("/api/ws/test-user-delete-notification")
    @ResponseBody
    public Map<String, String> testUserDeleteNotification() {
        try {
            logger.info("Enviando notificación de prueba de eliminación de usuario vía WebSocket");
            
            // Crear un usuario de prueba eliminado
            Map<String, Object> testUserDelete = Map.of(
                "type", "user_deleted",
                "user_id", "test-uuid-123",
                "email", "deleted@example.com",
                "role", "user",
                "timestamp", System.currentTimeMillis()
            );
            
            // Enviar heartbeat
            webSocketNotificationService.sendHeartbeat();
            
            // Enviar notificación de usuario eliminado
            webSocketNotificationService.notifyUserDeleted(testUserDelete);
            
            return Map.of(
                "status", "success",
                "message", "Notificación de prueba de eliminación de usuario enviada",
                "timestamp", String.valueOf(System.currentTimeMillis())
            );
            
        } catch (Exception e) {
            logger.error("Error al enviar notificación de prueba de eliminación de usuario", e);
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
                    "/topic/appointments/new",
                    "/topic/appointments/updated",
                    "/topic/appointments/cancelled",
                    "/topic/appointments/status-changed",
                    "/topic/appointments/by-date-consulted",
                    "/topic/contacts/new",
                    "/topic/contacts/updated",
                    "/topic/contacts/deleted",
                    "/topic/contacts/consulted",
                    "/topic/calls/new",
                    "/topic/calls/updated",
                    "/topic/calls/deleted",
                    "/topic/calls/consulted",
                    "/topic/calls/stats/consulted",
                    "/topic/dashboard/stats/consulted",
                    "/topic/dashboard/metrics/updated",
                    "/topic/users/new",
                    "/topic/users/consulted",
                    "/topic/users/updated",
                    "/topic/users/deleted",
                    "/topic/user/consulted",
                    "/topic/heartbeat",
                    "/topic/greetings"
                },
                "userTopics", new String[]{
                    "/user/{userId}/requests/new",
                    "/user/{userId}/requests/updated"
                },
                "phoneTopics", new String[]{
                    "/topic/phone/{phoneNumber}/appointments/new",
                    "/topic/phone/{phoneNumber}/appointments/updated",
                    "/topic/phone/{phoneNumber}/appointments/cancelled",
                    "/topic/phone/{phoneNumber}/appointments/status-changed",
                    "/topic/phone/{phoneNumber}/contacts/new",
                    "/topic/phone/{phoneNumber}/contacts/updated",
                    "/topic/phone/{phoneNumber}/contacts/deleted"
                },
                "emailTopics", new String[]{
                    "/topic/email/{email}/contacts/new",
                    "/topic/email/{email}/contacts/updated",
                    "/topic/email/{email}/contacts/deleted"
                },
                "contactTopics", new String[]{
                    "/topic/contact/{contactId}/calls/new",
                    "/topic/contact/{contactId}/calls/updated",
                    "/topic/contact/{contactId}/calls/deleted"
                }
            ),
            "timestamp", System.currentTimeMillis()
        );
    }
}
