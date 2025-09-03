package com.lucia.service;

import com.lucia.entity.Call;
import com.lucia.entity.Contact;
import com.lucia.dto.CallCreateDto;
import com.lucia.dto.CallUpdateDto;
import com.lucia.repository.CallRepository;
import com.lucia.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CallService {

    @Autowired
    private CallRepository callRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private WebSocketNotificationService webSocketNotificationService;

    // Crear una nueva llamada
    public Call createCall(CallCreateDto callDto) {
        // Validar que la duración sea positiva
        if (callDto.getDuration() != null && callDto.getDuration() <= 0) {
            throw new IllegalArgumentException("La duración debe ser un número positivo");
        }
        
        // Convertir DTO a entidad
        Call call = new Call();
        // La fecha se establecerá automáticamente en la entidad con @PrePersist
        call.setDuration(callDto.getDuration());
        call.setMotive(callDto.getMotive());
        call.setSummary(callDto.getSummary());
        call.setIntent(callDto.getIntent());
        call.setMessages(callDto.getMessages());
        call.setAudioCombined(callDto.getAudioCombined());
        call.setAudioAssistant(callDto.getAudioAssistant());
        call.setAudioCustomer(callDto.getAudioCustomer());
        
        // Validar que el contacto existe si se proporciona
        if (callDto.getContactId() != null) {
            Optional<Contact> contact = contactRepository.findById(callDto.getContactId());
            if (!contact.isPresent()) {
                throw new IllegalArgumentException("El contacto especificado no existe");
            }
            call.setContact(contact.get());
        }
        
        Call savedCall = callRepository.save(call);
        
        // Notificar vía WebSocket
        webSocketNotificationService.notifyNewCall(savedCall);
        
        return savedCall;
    }

    // Obtener todas las llamadas
    public List<Call> getAllCalls() {
        List<Call> calls = callRepository.findAll();
        
        // Notificar vía WebSocket sobre la consulta
        webSocketNotificationService.notifyCallsConsulted(calls);
        
        return calls;
    }

    // Obtener una llamada por ID
    public Optional<Call> getCallById(Long id) {
        return callRepository.findById(id);
    }

    // Actualizar una llamada
    public Call updateCall(Long id, CallUpdateDto callDto) {
        Optional<Call> optionalCall = callRepository.findById(id);
        
        if (optionalCall.isPresent()) {
            Call existingCall = optionalCall.get();
            
            // Validar que la duración sea positiva si se proporciona
            if (callDto.getDuration() != null && callDto.getDuration() <= 0) {
                throw new IllegalArgumentException("La duración debe ser un número positivo");
            }
            
            // Validar que la fecha no sea anterior a hoy solo si se proporciona
            if (callDto.getDate() != null && callDto.getDate().toLocalDate().isBefore(LocalDateTime.now().toLocalDate())) {
                throw new IllegalArgumentException("No se pueden actualizar llamadas con fechas pasadas");
            }
            
            // Actualizar campos si no son null
            if (callDto.getDate() != null) {
                existingCall.setDate(callDto.getDate());
            }
            if (callDto.getDuration() != null) {
                existingCall.setDuration(callDto.getDuration());
            }
            if (callDto.getMotive() != null) {
                existingCall.setMotive(callDto.getMotive());
            }
            if (callDto.getContactId() != null) {
                // Validar que el contacto existe si se proporciona
                Optional<Contact> contact = contactRepository.findById(callDto.getContactId());
                if (!contact.isPresent()) {
                    throw new IllegalArgumentException("El contacto especificado no existe");
                }
                existingCall.setContact(contact.get());
            }
            if (callDto.getSummary() != null) {
                existingCall.setSummary(callDto.getSummary());
            }
            if (callDto.getIntent() != null) {
                existingCall.setIntent(callDto.getIntent());
            }
            if (callDto.getMessages() != null) {
                existingCall.setMessages(callDto.getMessages());
            }
            if (callDto.getAudioCombined() != null) {
                existingCall.setAudioCombined(callDto.getAudioCombined());
            }
            if (callDto.getAudioAssistant() != null) {
                existingCall.setAudioAssistant(callDto.getAudioAssistant());
            }
            if (callDto.getAudioCustomer() != null) {
                existingCall.setAudioCustomer(callDto.getAudioCustomer());
            }
            
            Call updatedCall = callRepository.save(existingCall);
            
            // Notificar vía WebSocket
            webSocketNotificationService.notifyCallUpdated(updatedCall);
            
            return updatedCall;
        } else {
            throw new RuntimeException("Llamada no encontrada con ID: " + id);
        }
    }

    // Eliminar una llamada
    public void deleteCall(Long id) {
        Optional<Call> optionalCall = callRepository.findById(id);
        if (optionalCall.isPresent()) {
            Call call = optionalCall.get();
            callRepository.deleteById(id);
            
            // Notificar vía WebSocket
            webSocketNotificationService.notifyCallDeleted(call);
        } else {
            throw new RuntimeException("Llamada no encontrada con ID: " + id);
        }
    }

    // Buscar llamadas por contacto
    public List<Call> getCallsByContactId(Long contactId) {
        return callRepository.findByContactId(contactId);
    }

    // Buscar llamadas por fecha
    public List<Call> getCallsByDate(LocalDateTime date) {
        return callRepository.findByDate(date);
    }

    // Buscar llamadas en un rango de fechas
    public List<Call> getCallsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return callRepository.findByDateBetween(startDate, endDate);
    }

    // Buscar llamadas por motivo
    public List<Call> getCallsByMotive(String motive) {
        return callRepository.findByMotiveContainingIgnoreCase(motive);
    }

    // Buscar llamadas por intención
    public List<Call> getCallsByIntent(String intent) {
        return callRepository.findByIntentContainingIgnoreCase(intent);
    }

    // Buscar llamadas por duración mínima
    public List<Call> getCallsByMinDuration(Integer minDuration) {
        return callRepository.findByDurationGreaterThanEqual(minDuration);
    }

    // Buscar llamadas por duración máxima
    public List<Call> getCallsByMaxDuration(Integer maxDuration) {
        return callRepository.findByDurationLessThanEqual(maxDuration);
    }

    // Buscar llamadas por rango de duración
    public List<Call> getCallsByDurationRange(Integer minDuration, Integer maxDuration) {
        return callRepository.findByDurationBetween(minDuration, maxDuration);
    }

    // Buscar llamadas que tengan audio
    public List<Call> getCallsWithAudio() {
        return callRepository.findByAudioCombinedIsNotNull();
    }

    // Buscar llamadas que tengan transcripción
    public List<Call> getCallsWithTranscription() {
        return callRepository.findByMessagesIsNotNull();
    }

    // Buscar llamadas por resumen o motivo
    public List<Call> searchCallsBySummaryOrMotive(String searchTerm) {
        return callRepository.findBySummaryContainingIgnoreCaseOrMotiveContainingIgnoreCase(searchTerm, searchTerm);
    }

    // Buscar llamadas por contacto y fecha
    public List<Call> getCallsByContactAndDate(Long contactId, LocalDateTime date) {
        return callRepository.findByContactIdAndDate(contactId, date);
    }

    // Buscar llamadas por contacto en un rango de fechas
    public List<Call> getCallsByContactAndDateRange(Long contactId, LocalDateTime startDate, LocalDateTime endDate) {
        return callRepository.findByContactIdAndDateBetween(contactId, startDate, endDate);
    }

    // Contar llamadas por contacto
    public Long countCallsByContactId(Long contactId) {
        return callRepository.countByContactId(contactId);
    }

    // Obtener duración total de llamadas por contacto
    public Long getTotalDurationByContactId(Long contactId) {
        return callRepository.getTotalDurationByContactId(contactId);
    }

    // Obtener llamadas más recientes por contacto
    public List<Call> getRecentCallsByContactId(Long contactId) {
        return callRepository.findTop5ByContactIdOrderByDateDesc(contactId);
    }

    // Obtener llamadas más largas
    public List<Call> getLongestCalls() {
        return callRepository.findTop10ByOrderByDurationDesc();
    }

    // Buscar llamadas por texto en resumen, motivo o intención
    public List<Call> searchCalls(String searchTerm) {
        return callRepository.findBySearchTerm(searchTerm);
    }

    // Obtener estadísticas de llamadas por contacto
    public List<Object[]> getCallStatsByContact() {
        return callRepository.getCallStatsByContact();
    }

    // Obtener llamadas del día actual
    public List<Call> getTodayCalls() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        return callRepository.findTodayCalls(startOfDay, endOfDay);
    }

    // Obtener llamadas de la semana actual
    public List<Call> getThisWeekCalls() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);
        
        return callRepository.findThisWeekCalls(startOfWeek, endOfWeek);
    }

    // Obtener estadísticas generales de llamadas
    public java.util.Map<String, Object> getCallStatistics() {
        List<Call> allCalls = callRepository.findAll();
        
        if (allCalls.isEmpty()) {
            return java.util.Map.of(
                "totalCalls", 0,
                "totalDuration", 0,
                "averageDuration", 0.0,
                "callsWithAudio", 0,
                "callsWithTranscription", 0
            );
        }
        
        int totalCalls = allCalls.size();
        long totalDuration = allCalls.stream().mapToLong(call -> call.getDuration() != null ? call.getDuration() : 0).sum();
        double averageDuration = totalCalls > 0 ? (double) totalDuration / totalCalls : 0.0;
        long callsWithAudio = allCalls.stream().filter(call -> call.getAudioCombined() != null).count();
        long callsWithTranscription = allCalls.stream().filter(call -> call.getMessages() != null).count();
        
        return java.util.Map.of(
            "totalCalls", totalCalls,
            "totalDuration", totalDuration,
            "averageDuration", averageDuration,
            "callsWithAudio", callsWithAudio,
            "callsWithTranscription", callsWithTranscription
        );
    }
}
