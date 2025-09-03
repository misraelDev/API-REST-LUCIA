package com.lucia.controller;

import com.lucia.dto.DashboardStatsDto;
import com.lucia.service.StatsService;
import com.lucia.service.WebSocketNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatsController {

    private final StatsService statsService;
    private final WebSocketNotificationService webSocketNotificationService;

    public StatsController(StatsService statsService, WebSocketNotificationService webSocketNotificationService) {
        this.statsService = statsService;
        this.webSocketNotificationService = webSocketNotificationService;
    }

    /**
     * Endpoint principal del dashboard que devuelve todas las métricas
     * GET /api/stats/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        try {
            // Obtener todas las métricas principales
            long totalCalls = statsService.getTotalCalls();
            long totalContacts = statsService.getTotalContacts();
            long totalAppointments = statsService.getTotalAppointments();
            double averageCallDuration = statsService.getAverageCallDuration();
            
            // Obtener datos históricos (por defecto 1 año)
            List<Map<String, Object>> historicalData = statsService.getHistoricalData("1 año");
            
            // Obtener conversaciones recientes
            List<Map<String, Object>> topConversations = statsService.getTopConversations();
            
            // Obtener motivos frecuentes
            List<Map<String, Object>> frequentMotives = statsService.getTopFrequentMotives();
            
            // Obtener contactos recientes
            List<Map<String, Object>> recentContacts = statsService.getRecentContacts();
            
            // Obtener resumen del último año
            Map<String, Object> summary = statsService.getCallsAndAppointmentsSummary();
            
            // Crear el DTO de respuesta
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
            
            // Enviar notificación WebSocket sobre la consulta del dashboard
            webSocketNotificationService.notifyDashboardStatsConsulted(dashboardStats);
            
            return ResponseEntity.ok(dashboardStats);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
