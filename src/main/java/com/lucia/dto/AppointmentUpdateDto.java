package com.lucia.dto;

import jakarta.validation.constraints.FutureOrPresent;
import com.lucia.entity.Appointment;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentUpdateDto {

    private String summary;

    private LocalTime startTime;

    private LocalTime endTime;

    @FutureOrPresent(message = "La fecha de la cita debe ser hoy o en el futuro")
    private LocalDate date;

    private Appointment.AppointmentStatus status;

    private String description;

    private String location;

    private String contactPhone;

    // Getters y Setters
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Appointment.AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(Appointment.AppointmentStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}
