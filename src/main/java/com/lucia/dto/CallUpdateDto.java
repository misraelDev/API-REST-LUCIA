package com.lucia.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

public class CallUpdateDto {

    private LocalDateTime date; // Opcional en actualización

    @Positive(message = "La duración debe ser un número positivo")
    private Integer duration; // Duración en segundos

    @Size(max = 500, message = "El motivo no puede exceder 500 caracteres")
    private String motive; // Motivo de la llamada

    private Long contactId; // ID del contacto al que se llamó

    @Size(max = 1000, message = "El resumen no puede exceder 1000 caracteres")
    private String summary; // Resumen de la llamada

    @Size(max = 200, message = "La intención no puede exceder 200 caracteres")
    private String intent; // Intención detectada

    private String messages; // JSON con la transcripción

    @Size(max = 500, message = "La URL del audio combinado no puede exceder 500 caracteres")
    private String audioCombined; // URL del audio completo

    @Size(max = 500, message = "La URL del audio del asistente no puede exceder 500 caracteres")
    private String audioAssistant; // URL del audio del asistente

    @Size(max = 500, message = "La URL del audio del cliente no puede exceder 500 caracteres")
    private String audioCustomer; // URL del audio del cliente

    // Getters y Setters
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

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
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
}
