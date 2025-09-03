package com.lucia.service;

import com.lucia.entity.Call;
import com.lucia.entity.Contact;
import com.lucia.entity.Appointment;
import com.lucia.repository.CallRepository;
import com.lucia.repository.ContactRepository;
import com.lucia.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final CallRepository callRepository;
    private final ContactRepository contactRepository;
    private final AppointmentRepository appointmentRepository;

    public StatsService(CallRepository callRepository, 
                       ContactRepository contactRepository, 
                       AppointmentRepository appointmentRepository) {
        this.callRepository = callRepository;
        this.contactRepository = contactRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Obtiene el total de llamadas
     */
    public long getTotalCalls() {
        return callRepository.count();
    }

    /**
     * Obtiene el total de contactos
     */
    public long getTotalContacts() {
        return contactRepository.count();
    }

    /**
     * Obtiene el total de citas
     */
    public long getTotalAppointments() {
        return appointmentRepository.count();
    }

    /**
     * Obtiene la duración promedio de las llamadas en segundos
     */
    public double getAverageCallDuration() {
        Double avgDuration = callRepository.findAverageDuration();
        return avgDuration != null ? avgDuration : 0.0;
    }

    /**
     * Obtiene las conversaciones recientes (top 5)
     */
    public List<Map<String, Object>> getTopConversations() {
        List<Call> calls = callRepository.findTop5ByOrderByDateDesc();
        
        return calls.stream()
                .map(call -> {
                    Map<String, Object> conversation = new HashMap<>();
                    conversation.put("contact_name", call.getContact() != null ? call.getContact().getName() : "Sin contacto");
                    conversation.put("motive", call.getMotive() != null ? call.getMotive() : "Sin motivo");
                    conversation.put("duration", call.getDuration() != null ? call.getDuration() : 0);
                    conversation.put("date", call.getDate().toString());
                    return conversation;
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los motivos más frecuentes de las llamadas
     */
    public List<Map<String, Object>> getTopFrequentMotives() {
        List<Object[]> motiveCounts = callRepository.findTopMotivesWithCount(5);
        
        return motiveCounts.stream()
                .map(row -> {
                    Map<String, Object> motive = new HashMap<>();
                    motive.put("motive", row[0]);
                    motive.put("total_calls", row[1]);
                    return motive;
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los contactos recientes (top 5)
     */
    public List<Map<String, Object>> getRecentContacts() {
        List<Contact> contacts = contactRepository.findTop5ByOrderByCreatedAtDesc();
        
        return contacts.stream()
                .map(contact -> {
                    Map<String, Object> contactData = new HashMap<>();
                    contactData.put("name", contact.getName());
                    contactData.put("email", contact.getEmail());
                    contactData.put("phone_number", contact.getPhoneNumber());
                    contactData.put("creation_date", contact.getCreatedAt().toString());
                    return contactData;
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene datos históricos para el gráfico según el período
     */
    public List<Map<String, Object>> getHistoricalData(String timeRange) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate;
        
        switch (timeRange) {
            case "7 días":
                startDate = endDate.minusDays(7);
                return getDailyData(startDate, endDate);
            case "30 días":
                startDate = endDate.minusDays(30);
                return getDailyData(startDate, endDate);
            case "3 meses":
                startDate = endDate.minusMonths(3);
                return getMonthlyData(startDate, endDate);
            case "1 año":
            default:
                startDate = endDate.minusYears(1);
                return getMonthlyData(startDate, endDate);
        }
    }

    /**
     * Obtiene datos diarios para períodos cortos
     */
    private List<Map<String, Object>> getDailyData(LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> data = new ArrayList<>();
        LocalDate endLocalDate = endDate.toLocalDate();
        LocalDate currentDate = startDate.toLocalDate();
        
        while (!currentDate.isAfter(endLocalDate)) {
            LocalDateTime dayStart = currentDate.atStartOfDay();
            LocalDateTime dayEnd = currentDate.plusDays(1).atStartOfDay();
            
            long callsCount = callRepository.countByDateBetween(dayStart, dayEnd);
            long appointmentsCount = appointmentRepository.countByDate(currentDate);
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("month", currentDate.format(DateTimeFormatter.ofPattern("MMM dd")));
            dayData.put("llamadas", callsCount);
            dayData.put("citas", appointmentsCount);
            data.add(dayData);
            
            currentDate = currentDate.plusDays(1);
        }
        
        return data;
    }

    /**
     * Obtiene datos mensuales para períodos largos
     */
    private List<Map<String, Object>> getMonthlyData(LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> data = new ArrayList<>();
        LocalDate endLocalDate = endDate.toLocalDate().withDayOfMonth(1);
        LocalDate currentDate = startDate.toLocalDate().withDayOfMonth(1);
        
        while (!currentDate.isAfter(endLocalDate)) {
            LocalDateTime monthStart = currentDate.atStartOfDay();
            LocalDateTime monthEnd = currentDate.plusMonths(1).atStartOfDay();
            
            long callsCount = callRepository.countByDateBetween(monthStart, monthEnd);
            long appointmentsCount = appointmentRepository.countByYearAndMonth(
                currentDate.getYear(), currentDate.getMonthValue());
            
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", currentDate.format(DateTimeFormatter.ofPattern("MMM yyyy")));
            monthData.put("llamadas", callsCount);
            monthData.put("citas", appointmentsCount);
            data.add(monthData);
            
            currentDate = currentDate.plusMonths(1);
        }
        
        return data;
    }

    /**
     * Obtiene el resumen de llamadas y citas del último año
     */
    public Map<String, Object> getCallsAndAppointmentsSummary() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        LocalDate oneYearAgoDate = oneYearAgo.toLocalDate();
        
        long callsLastYear = callRepository.countByDateAfter(oneYearAgo);
        long appointmentsLastYear = appointmentRepository.countByDateAfter(oneYearAgoDate);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("llamadas_ultimo_anio", callsLastYear);
        summary.put("citas_ultimo_anio", appointmentsLastYear);
        
        return summary;
    }
}
