package com.lucia.service;

import com.lucia.entity.Appointment;
import com.lucia.repository.AppointmentRepository;
import com.lucia.service.WebSocketNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private WebSocketNotificationService webSocketNotificationService;

    // Crear una nueva cita
    public Appointment createAppointment(Appointment appointment) {
        // Validar que la hora de fin sea posterior a la hora de inicio
        if (appointment.getStartTime().isAfter(appointment.getEndTime())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }
        
        // Validar que la fecha no sea anterior a hoy
        if (appointment.getDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se pueden crear citas en fechas pasadas");
        }
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        // Notificar vía WebSocket
        webSocketNotificationService.notifyNewAppointment(savedAppointment);
        
        return savedAppointment;
    }

    // Obtener todas las citas
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // Obtener una cita por ID
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    // Actualizar una cita
    public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);
        
        if (optionalAppointment.isPresent()) {
            Appointment existingAppointment = optionalAppointment.get();
            
            // Validar que la hora de fin sea posterior a la hora de inicio
            if (appointmentDetails.getStartTime() != null && appointmentDetails.getEndTime() != null) {
                if (appointmentDetails.getStartTime().isAfter(appointmentDetails.getEndTime())) {
                    throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
                }
            }
            
            // Actualizar campos si no son null
            if (appointmentDetails.getSummary() != null) {
                existingAppointment.setSummary(appointmentDetails.getSummary());
            }
            if (appointmentDetails.getStartTime() != null) {
                existingAppointment.setStartTime(appointmentDetails.getStartTime());
            }
            if (appointmentDetails.getEndTime() != null) {
                existingAppointment.setEndTime(appointmentDetails.getEndTime());
            }
            if (appointmentDetails.getDate() != null) {
                existingAppointment.setDate(appointmentDetails.getDate());
            }
            if (appointmentDetails.getStatus() != null) {
                existingAppointment.setStatus(appointmentDetails.getStatus());
            }
            if (appointmentDetails.getDescription() != null) {
                existingAppointment.setDescription(appointmentDetails.getDescription());
            }
            if (appointmentDetails.getLocation() != null) {
                existingAppointment.setLocation(appointmentDetails.getLocation());
            }
            if (appointmentDetails.getContactPhone() != null) {
                existingAppointment.setContactPhone(appointmentDetails.getContactPhone());
            }
            
            Appointment updatedAppointment = appointmentRepository.save(existingAppointment);
            
            // Notificar vía WebSocket
            webSocketNotificationService.notifyAppointmentUpdated(updatedAppointment);
            
            return updatedAppointment;
        } else {
            throw new RuntimeException("Cita no encontrada con ID: " + id);
        }
    }

    // Eliminar una cita
    public void deleteAppointment(Long id) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            appointmentRepository.deleteById(id);
            
            // Notificar vía WebSocket
            webSocketNotificationService.notifyAppointmentCancelled(appointment);
        } else {
            throw new RuntimeException("Cita no encontrada con ID: " + id);
        }
    }

    // Buscar citas por fecha
    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        List<Appointment> appointments = appointmentRepository.findByDate(date);
        
        // Notificar vía WebSocket sobre la consulta
        webSocketNotificationService.notifyAppointmentsByDateConsulted(date, appointments);
        
        return appointments;
    }

    // Buscar citas por estado
    public List<Appointment> getAppointmentsByStatus(Appointment.AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }

    // Buscar citas por fecha y estado
    public List<Appointment> getAppointmentsByDateAndStatus(LocalDate date, Appointment.AppointmentStatus status) {
        return appointmentRepository.findByDateAndStatus(date, status);
    }

    // Buscar citas en un rango de fechas
    public List<Appointment> getAppointmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findByDateRange(startDate, endDate);
    }

    // Buscar citas por teléfono de contacto
    public List<Appointment> getAppointmentsByContactPhone(String contactPhone) {
        return appointmentRepository.findByContactPhone(contactPhone);
    }

    // Buscar citas por texto en resumen o descripción
    public List<Appointment> searchAppointments(String searchTerm) {
        return appointmentRepository.findBySummaryOrDescriptionContaining(searchTerm);
    }

    // Cambiar estado de una cita
    public Appointment changeAppointmentStatus(Long id, Appointment.AppointmentStatus newStatus) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);
        
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            String oldStatus = appointment.getStatus().name();
            appointment.setStatus(newStatus);
            
            Appointment updatedAppointment = appointmentRepository.save(appointment);
            
            // Notificar vía WebSocket
            webSocketNotificationService.notifyAppointmentStatusChanged(updatedAppointment, oldStatus, newStatus.name());
            
            return updatedAppointment;
        } else {
            throw new RuntimeException("Cita no encontrada con ID: " + id);
        }
    }

    // Verificar disponibilidad en una fecha y hora específica
    public boolean isTimeSlotAvailable(LocalDate date, LocalTime startTime, LocalTime endTime, Long excludeAppointmentId) {
        List<Appointment> appointmentsOnDate = appointmentRepository.findByDate(date);
        
        for (Appointment appointment : appointmentsOnDate) {
            if (excludeAppointmentId != null && appointment.getId().equals(excludeAppointmentId)) {
                continue; // Excluir la cita actual si se está editando
            }
            
            // Verificar si hay solapamiento de horarios
            if (!(endTime.isBefore(appointment.getStartTime()) || startTime.isAfter(appointment.getEndTime()))) {
                return false; // Hay solapamiento
            }
        }
        
        return true; // No hay solapamiento
    }
}
