package com.lucia.dto;

import java.util.List;

public class DashboardResponseDto {
    
    // Métricas principales
    private Long totalCalls;
    private Long totalContacts;
    private Long totalAppointments;
    private String averageCallDuration; // Formato "MM:SS"
    
    // Datos históricos para gráficos
    private List<HistoricalDataDto> historicalData;
    
    // Conversaciones recientes
    private List<RecentConversationDto> recentConversations;
    
    // Motivos frecuentes
    private List<FrequentMotiveDto> frequentMotives;
    
    // Contactos recientes
    private List<RecentContactDto> recentContacts;
    
    // Constructor por defecto
    public DashboardResponseDto() {}
    
    // Constructor con todos los parámetros
    public DashboardResponseDto(Long totalCalls, Long totalContacts, Long totalAppointments, 
                               String averageCallDuration, List<HistoricalDataDto> historicalData,
                               List<RecentConversationDto> recentConversations,
                               List<FrequentMotiveDto> frequentMotives,
                               List<RecentContactDto> recentContacts) {
        this.totalCalls = totalCalls;
        this.totalContacts = totalContacts;
        this.totalAppointments = totalAppointments;
        this.averageCallDuration = averageCallDuration;
        this.historicalData = historicalData;
        this.recentConversations = recentConversations;
        this.frequentMotives = frequentMotives;
        this.recentContacts = recentContacts;
    }
    
    // Getters y Setters
    public Long getTotalCalls() {
        return totalCalls;
    }
    
    public void setTotalCalls(Long totalCalls) {
        this.totalCalls = totalCalls;
    }
    
    public Long getTotalContacts() {
        return totalContacts;
    }
    
    public void setTotalContacts(Long totalContacts) {
        this.totalContacts = totalContacts;
    }
    
    public Long getTotalAppointments() {
        return totalAppointments;
    }
    
    public void setTotalAppointments(Long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }
    
    public String getAverageCallDuration() {
        return averageCallDuration;
    }
    
    public void setAverageCallDuration(String averageCallDuration) {
        this.averageCallDuration = averageCallDuration;
    }
    
    public List<HistoricalDataDto> getHistoricalData() {
        return historicalData;
    }
    
    public void setHistoricalData(List<HistoricalDataDto> historicalData) {
        this.historicalData = historicalData;
    }
    
    public List<RecentConversationDto> getRecentConversations() {
        return recentConversations;
    }
    
    public void setRecentConversations(List<RecentConversationDto> recentConversations) {
        this.recentConversations = recentConversations;
    }
    
    public List<FrequentMotiveDto> getFrequentMotives() {
        return frequentMotives;
    }
    
    public void setFrequentMotives(List<FrequentMotiveDto> frequentMotives) {
        this.frequentMotives = frequentMotives;
    }
    
    public List<RecentContactDto> getRecentContacts() {
        return recentContacts;
    }
    
    public void setRecentContacts(List<RecentContactDto> recentContacts) {
        this.recentContacts = recentContacts;
    }
    
    // DTOs internos
    public static class HistoricalDataDto {
        private String month;
        private Long llamadas;
        private Long citas;
        
        public HistoricalDataDto() {}
        
        public HistoricalDataDto(String month, Long llamadas, Long citas) {
            this.month = month;
            this.llamadas = llamadas;
            this.citas = citas;
        }
        
        // Getters y Setters
        public String getMonth() {
            return month;
        }
        
        public void setMonth(String month) {
            this.month = month;
        }
        
        public Long getLlamadas() {
            return llamadas;
        }
        
        public void setLlamadas(Long llamadas) {
            this.llamadas = llamadas;
        }
        
        public Long getCitas() {
            return citas;
        }
        
        public void setCitas(Long citas) {
            this.citas = citas;
        }
    }
    
    public static class RecentConversationDto {
        private String contactName;
        private String date;
        private String motive;
        private Integer duration; // en segundos
        
        public RecentConversationDto() {}
        
        public RecentConversationDto(String contactName, String date, String motive, Integer duration) {
            this.contactName = contactName;
            this.date = date;
            this.motive = motive;
            this.duration = duration;
        }
        
        // Getters y Setters
        public String getContactName() {
            return contactName;
        }
        
        public void setContactName(String contactName) {
            this.contactName = contactName;
        }
        
        public String getDate() {
            return date;
        }
        
        public void setDate(String date) {
            this.date = date;
        }
        
        public String getMotive() {
            return motive;
        }
        
        public void setMotive(String motive) {
            this.motive = motive;
        }
        
        public Integer getDuration() {
            return duration;
        }
        
        public void setDuration(Integer duration) {
            this.duration = duration;
        }
    }
    
    public static class FrequentMotiveDto {
        private String motive;
        private Long totalCalls;
        
        public FrequentMotiveDto() {}
        
        public FrequentMotiveDto(String motive, Long totalCalls) {
            this.motive = motive;
            this.totalCalls = totalCalls;
        }
        
        // Getters y Setters
        public String getMotive() {
            return motive;
        }
        
        public void setMotive(String motive) {
            this.motive = motive;
        }
        
        public Long getTotalCalls() {
            return totalCalls;
        }
        
        public void setTotalCalls(Long totalCalls) {
            this.totalCalls = totalCalls;
        }
    }
    
    public static class RecentContactDto {
        private String name;
        private String email;
        private String phoneNumber;
        private String creationDate;
        
        public RecentContactDto() {}
        
        public RecentContactDto(String name, String email, String phoneNumber, String creationDate) {
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.creationDate = creationDate;
        }
        
        // Getters y Setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPhoneNumber() {
            return phoneNumber;
        }
        
        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
        
        public String getCreationDate() {
            return creationDate;
        }
        
        public void setCreationDate(String creationDate) {
            this.creationDate = creationDate;
        }
    }
}
