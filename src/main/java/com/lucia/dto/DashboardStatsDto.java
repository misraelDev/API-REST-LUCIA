package com.lucia.dto;

import java.util.List;
import java.util.Map;

public class DashboardStatsDto {
    
    // Métricas principales
    private long totalCalls;
    private long totalContacts;
    private long totalAppointments;
    private double averageCallDuration;
    
    // Datos históricos para gráficos
    private List<Map<String, Object>> historicalData;
    
    // Conversaciones recientes
    private List<Map<String, Object>> topConversations;
    
    // Motivos frecuentes
    private List<Map<String, Object>> frequentMotives;
    
    // Contactos recientes
    private List<Map<String, Object>> recentContacts;
    
    // Resumen del último año
    private Map<String, Object> summary;

    // Constructores
    public DashboardStatsDto() {}

    public DashboardStatsDto(long totalCalls, long totalContacts, long totalAppointments, 
                           double averageCallDuration, List<Map<String, Object>> historicalData,
                           List<Map<String, Object>> topConversations, 
                           List<Map<String, Object>> frequentMotives,
                           List<Map<String, Object>> recentContacts,
                           Map<String, Object> summary) {
        this.totalCalls = totalCalls;
        this.totalContacts = totalContacts;
        this.totalAppointments = totalAppointments;
        this.averageCallDuration = averageCallDuration;
        this.historicalData = historicalData;
        this.topConversations = topConversations;
        this.frequentMotives = frequentMotives;
        this.recentContacts = recentContacts;
        this.summary = summary;
    }

    // Getters y Setters
    public long getTotalCalls() {
        return totalCalls;
    }

    public void setTotalCalls(long totalCalls) {
        this.totalCalls = totalCalls;
    }

    public long getTotalContacts() {
        return totalContacts;
    }

    public void setTotalContacts(long totalContacts) {
        this.totalContacts = totalContacts;
    }

    public long getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public double getAverageCallDuration() {
        return averageCallDuration;
    }

    public void setAverageCallDuration(double averageCallDuration) {
        this.averageCallDuration = averageCallDuration;
    }

    public List<Map<String, Object>> getHistoricalData() {
        return historicalData;
    }

    public void setHistoricalData(List<Map<String, Object>> historicalData) {
        this.historicalData = historicalData;
    }

    public List<Map<String, Object>> getTopConversations() {
        return topConversations;
    }

    public void setTopConversations(List<Map<String, Object>> topConversations) {
        this.topConversations = topConversations;
    }

    public List<Map<String, Object>> getFrequentMotives() {
        return frequentMotives;
    }

    public void setFrequentMotives(List<Map<String, Object>> frequentMotives) {
        this.frequentMotives = frequentMotives;
    }

    public List<Map<String, Object>> getRecentContacts() {
        return recentContacts;
    }

    public void setRecentContacts(List<Map<String, Object>> recentContacts) {
        this.recentContacts = recentContacts;
    }

    public Map<String, Object> getSummary() {
        return summary;
    }

    public void setSummary(Map<String, Object> summary) {
        this.summary = summary;
    }
}
