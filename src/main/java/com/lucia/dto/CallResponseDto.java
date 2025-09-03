package com.lucia.dto;

import com.lucia.entity.Call;
import java.time.LocalDateTime;

public class CallResponseDto {

    private Long id;
    private LocalDateTime date;
    private Integer duration;
    private String motive;
    private String contactName; // Nombre del contacto si está disponible
    private String summary;
    private String intent;
    private String messages;
    private String audioCombined;
    private String audioAssistant;
    private String audioCustomer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor para convertir desde entidad Call
    public CallResponseDto(Call call) {
        this.id = call.getId();
        this.date = call.getDate();
        this.duration = call.getDuration();
        this.motive = call.getMotive();
        this.summary = call.getSummary();
        this.intent = call.getIntent();
        this.messages = call.getMessages();
        this.audioCombined = call.getAudioCombined();
        this.audioAssistant = call.getAudioAssistant();
        this.audioCustomer = call.getAudioCustomer();
        this.createdAt = call.getCreatedAt();
        this.updatedAt = call.getUpdatedAt();
        
        // Manejar la relación con Contact
        if (call.getContact() != null) {
            this.contactName = call.getContact().getName();
        }
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getMotive() {
        return motive;
    }

    public void setMotive(String motive) {
        this.motive = motive;
    }



    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public String getAudioCombined() {
        return audioCombined;
    }

    public void setAudioCombined(String audioCombined) {
        this.audioCombined = audioCombined;
    }

    public String getAudioAssistant() {
        return audioAssistant;
    }

    public void setAudioAssistant(String audioAssistant) {
        this.audioAssistant = audioAssistant;
    }

    public String getAudioCustomer() {
        return audioCustomer;
    }

    public void setAudioCustomer(String audioCustomer) {
        this.audioCustomer = audioCustomer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
