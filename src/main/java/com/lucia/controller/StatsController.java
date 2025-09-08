package com.lucia.controller;

import com.lucia.dto.DashboardStatsDto;
import com.lucia.service.StatsService;
import com.lucia.service.CallService;
import com.lucia.service.AppointmentService;
import com.lucia.dto.CallResponseDto;
import com.lucia.entity.Appointment;
import com.lucia.entity.Call;
import com.lucia.service.WebSocketNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatsController {

    private final StatsService statsService;
    private final WebSocketNotificationService webSocketNotificationService;
    private final CallService callService;
    private final AppointmentService appointmentService;

    public StatsController(StatsService statsService, WebSocketNotificationService webSocketNotificationService,
                          CallService callService, AppointmentService appointmentService) {
        this.statsService = statsService;
        this.webSocketNotificationService = webSocketNotificationService;
        this.callService = callService;
        this.appointmentService = appointmentService;
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
            
            // No incluir datos históricos
            List<Map<String, Object>> historicalData = Collections.emptyList();
            
            // Obtener conversaciones recientes
            List<Map<String, Object>> topConversations = statsService.getTopConversations();
            
            // Obtener motivos frecuentes
            List<Map<String, Object>> frequentMotives = statsService.getTopFrequentMotives();
            
            // Obtener contactos recientes
            List<Map<String, Object>> recentContacts = statsService.getRecentContacts();
            
            // No incluir resumen del último año
            Map<String, Object> summary = Collections.emptyMap();
            
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

    /**
     * Endpoint para obtener todas las citas y todas las llamadas en una sola respuesta
     * GET /api/stats/all
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllCallsAndAppointments() {
        try {
            List<Call> calls = callService.getAllCalls();
            List<CallResponseDto> callDtos = calls.stream().map(CallResponseDto::new).toList();
            List<Appointment> appointments = appointmentService.getAllAppointments();

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("calls", callDtos);
            result.put("appointments", appointments);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
