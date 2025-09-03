package com.lucia.controller;

import com.lucia.dto.CallCreateDto;
import com.lucia.dto.CallUpdateDto;
import com.lucia.dto.CallResponseDto;
import com.lucia.entity.Call;
import com.lucia.service.CallService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/calls")
@CrossOrigin(origins = "*")
public class CallController {

    @Autowired
    private CallService callService;

    // Crear una nueva llamada
    @PostMapping
    public ResponseEntity<?> createCall(@Valid @RequestBody CallCreateDto callDto) {
        try {
            Call createdCall = callService.createCall(callDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new CallResponseDto(createdCall));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    // Obtener todas las llamadas
    @GetMapping
    public ResponseEntity<List<CallResponseDto>> getAllCalls() {
        List<Call> calls = callService.getAllCalls();
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Obtener una llamada por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCallById(@PathVariable Long id) {
        Optional<Call> call = callService.getCallById(id);
        
        if (call.isPresent()) {
            return ResponseEntity.ok(new CallResponseDto(call.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Actualizar una llamada
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCall(@PathVariable Long id, @Valid @RequestBody CallUpdateDto callDto) {
        try {
            Call updatedCall = callService.updateCall(id, callDto);
            return ResponseEntity.ok(new CallResponseDto(updatedCall));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error de validación: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    // Eliminar una llamada
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCall(@PathVariable Long id) {
        try {
            callService.deleteCall(id);
            return ResponseEntity.ok("Llamada eliminada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    // Buscar llamadas por contacto
    @GetMapping("/contact/{contactId}")
    public ResponseEntity<List<CallResponseDto>> getCallsByContactId(@PathVariable Long contactId) {
        List<Call> calls = callService.getCallsByContactId(contactId);
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Buscar llamadas por fecha
    @GetMapping("/date/{date}")
    public ResponseEntity<List<CallResponseDto>> getCallsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        List<Call> calls = callService.getCallsByDate(date);
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Buscar llamadas en un rango de fechas
    @GetMapping("/date-range")
    public ResponseEntity<List<CallResponseDto>> getCallsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Call> calls = callService.getCallsByDateRange(startDate, endDate);
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Buscar llamadas por motivo
    @GetMapping("/motive/{motive}")
    public ResponseEntity<List<CallResponseDto>> getCallsByMotive(@PathVariable String motive) {
        List<Call> calls = callService.getCallsByMotive(motive);
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Buscar llamadas por intención
    @GetMapping("/intent/{intent}")
    public ResponseEntity<List<CallResponseDto>> getCallsByIntent(@PathVariable String intent) {
        List<Call> calls = callService.getCallsByIntent(intent);
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Buscar llamadas por duración mínima
    @GetMapping("/duration/min/{minDuration}")
    public ResponseEntity<List<CallResponseDto>> getCallsByMinDuration(@PathVariable Integer minDuration) {
        List<Call> calls = callService.getCallsByMinDuration(minDuration);
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Buscar llamadas por duración máxima
    @GetMapping("/duration/max/{maxDuration}")
    public ResponseEntity<List<CallResponseDto>> getCallsByMaxDuration(@PathVariable Integer maxDuration) {
        List<Call> calls = callService.getCallsByMaxDuration(maxDuration);
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Buscar llamadas por rango de duración
    @GetMapping("/duration/range")
    public ResponseEntity<List<CallResponseDto>> getCallsByDurationRange(
            @RequestParam Integer minDuration,
            @RequestParam Integer maxDuration) {
        List<Call> calls = callService.getCallsByDurationRange(minDuration, maxDuration);
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Buscar llamadas que tengan audio
    @GetMapping("/with-audio")
    public ResponseEntity<List<CallResponseDto>> getCallsWithAudio() {
        List<Call> calls = callService.getCallsWithAudio();
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Buscar llamadas que tengan transcripción
    @GetMapping("/with-transcription")
    public ResponseEntity<List<CallResponseDto>> getCallsWithTranscription() {
        List<Call> calls = callService.getCallsWithTranscription();
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Buscar llamadas por resumen o motivo
    @GetMapping("/search/summary-motive")
    public ResponseEntity<List<CallResponseDto>> searchCallsBySummaryOrMotive(@RequestParam String q) {
        List<Call> calls = callService.searchCallsBySummaryOrMotive(q);
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Buscar llamadas por contacto y fecha
    @GetMapping("/contact/{contactId}/date/{date}")
    public ResponseEntity<List<CallResponseDto>> getCallsByContactAndDate(
            @PathVariable Long contactId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        List<Call> calls = callService.getCallsByContactAndDate(contactId, date);
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Buscar llamadas por contacto en un rango de fechas
    @GetMapping("/contact/{contactId}/date-range")
    public ResponseEntity<List<CallResponseDto>> getCallsByContactAndDateRange(
            @PathVariable Long contactId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Call> calls = callService.getCallsByContactAndDateRange(contactId, startDate, endDate);
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Contar llamadas por contacto
    @GetMapping("/contact/{contactId}/count")
    public ResponseEntity<Long> countCallsByContactId(@PathVariable Long contactId) {
        Long count = callService.countCallsByContactId(contactId);
        return ResponseEntity.ok(count);
    }

    // Obtener duración total de llamadas por contacto
    @GetMapping("/contact/{contactId}/total-duration")
    public ResponseEntity<Long> getTotalDurationByContactId(@PathVariable Long contactId) {
        Long totalDuration = callService.getTotalDurationByContactId(contactId);
        return ResponseEntity.ok(totalDuration);
    }

    // Obtener llamadas más recientes por contacto
    @GetMapping("/contact/{contactId}/recent")
    public ResponseEntity<List<CallResponseDto>> getRecentCallsByContactId(@PathVariable Long contactId) {
        List<Call> calls = callService.getRecentCallsByContactId(contactId);
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Obtener llamadas más largas
    @GetMapping("/longest")
    public ResponseEntity<List<CallResponseDto>> getLongestCalls() {
        List<Call> calls = callService.getLongestCalls();
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Buscar llamadas por texto en resumen, motivo o intención
    @GetMapping("/search")
    public ResponseEntity<List<CallResponseDto>> searchCalls(@RequestParam String q) {
        List<Call> calls = callService.searchCalls(q);
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Obtener estadísticas de llamadas por contacto
    @GetMapping("/stats/by-contact")
    public ResponseEntity<List<Object[]>> getCallStatsByContact() {
        List<Object[]> stats = callService.getCallStatsByContact();
        return ResponseEntity.ok(stats);
    }

    // Obtener llamadas del día actual
    @GetMapping("/today")
    public ResponseEntity<List<CallResponseDto>> getTodayCalls() {
        List<Call> calls = callService.getTodayCalls();
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Obtener llamadas de la semana actual
    @GetMapping("/this-week")
    public ResponseEntity<List<CallResponseDto>> getThisWeekCalls() {
        List<Call> calls = callService.getThisWeekCalls();
        List<CallResponseDto> responseDtos = calls.stream()
                .map(CallResponseDto::new)
                .toList();
        return ResponseEntity.ok(responseDtos);
    }

    // Obtener estadísticas generales de llamadas
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getCallStatistics() {
        Map<String, Object> statistics = callService.getCallStatistics();
        return ResponseEntity.ok(statistics);
    }
}
