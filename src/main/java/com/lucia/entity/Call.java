package com.lucia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import jakarta.persistence.PrePersist;

@Entity
@Table(name = "calls")
public class Call extends BaseEntity {

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "duration", nullable = false)
    private Integer duration; // Duración en segundos

    @Column(name = "motive", length = 500)
    private String motive; // Motivo de la llamada

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact; // Contacto al que se llamó

    @Column(name = "summary", length = 1000)
    private String summary; // Resumen de la llamada

    @Column(name = "intent", length = 200)
    private String intent; // Intención detectada

    @Column(name = "messages", columnDefinition = "TEXT")
    private String messages; // JSON con la transcripción

    @Column(name = "audio_combined", length = 500)
    private String audioCombined; // URL del audio completo

    @Column(name = "audio_assistant", length = 500)
    private String audioAssistant; // URL del audio del asistente

    @Column(name = "audio_customer", length = 500)
    private String audioCustomer; // URL del audio del cliente

    // Método para establecer la fecha automáticamente antes de persistir
    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = LocalDateTime.now();
        }
    }

    // Constructores
    public Call() {}

    public Call(LocalDateTime date, Integer duration, String motive, Contact contact, 
                String summary, String intent, String messages, String audioCombined, 
                String audioAssistant, String audioCustomer) {
        this.date = date;
        this.duration = duration;
        this.motive = motive;
        this.contact = contact;
        this.summary = summary;
        this.intent = intent;
        this.messages = messages;
        this.audioCombined = audioCombined;
        this.audioAssistant = audioAssistant;
        this.audioCustomer = audioCustomer;
    }

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

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
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

    @Override
    public String toString() {
        return "Call{" +
                "id=" + getId() +
                ", date=" + date +
                ", duration=" + duration +
                ", motive='" + motive + '\'' +
                ", contact=" + (contact != null ? contact.getId() : null) +
                ", summary='" + summary + '\'' +
                ", intent='" + intent + '\'' +
                ", audioCombined='" + audioCombined + '\'' +
                ", audioAssistant='" + audioAssistant + '\'' +
                ", audioCustomer='" + audioCustomer + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
