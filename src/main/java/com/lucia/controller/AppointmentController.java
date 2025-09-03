package com.lucia.controller;

import com.lucia.dto.AppointmentCreateDto;
import com.lucia.dto.AppointmentUpdateDto;
import com.lucia.entity.Appointment;
import com.lucia.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // Crear una nueva cita
    @PostMapping
    public ResponseEntity<?> createAppointment(@Valid @RequestBody AppointmentCreateDto appointmentDto) {
        try {
            // Convertir DTO a entidad
            Appointment appointment = new Appointment();
            appointment.setSummary(appointmentDto.getSummary());
            appointment.setStartTime(appointmentDto.getStartTime());
            appointment.setEndTime(appointmentDto.getEndTime());
            appointment.setDate(appointmentDto.getDate());
            appointment.setDescription(appointmentDto.getDescription());
            appointment.setLocation(appointmentDto.getLocation());
            appointment.setContactPhone(appointmentDto.getContactPhone());
            appointment.setStatus(Appointment.AppointmentStatus.UNASSIGNED); // Estado por defecto
            
            Appointment createdAppointment = appointmentService.createAppointment(appointment);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    // Obtener todas las citas
    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    // Obtener una cita por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long id) {
        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);
        
        if (appointment.isPresent()) {
            return ResponseEntity.ok(appointment.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Actualizar una cita
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentUpdateDto appointmentDto) {
        try {
            // Convertir DTO a entidad
            Appointment appointmentDetails = new Appointment();
            appointmentDetails.setSummary(appointmentDto.getSummary());
            appointmentDetails.setStartTime(appointmentDto.getStartTime());
            appointmentDetails.setEndTime(appointmentDto.getEndTime());
            appointmentDetails.setDate(appointmentDto.getDate());
            appointmentDetails.setStatus(appointmentDto.getStatus());
            appointmentDetails.setDescription(appointmentDto.getDescription());
            appointmentDetails.setLocation(appointmentDto.getLocation());
            appointmentDetails.setContactPhone(appointmentDto.getContactPhone());
            
            Appointment updatedAppointment = appointmentService.updateAppointment(id, appointmentDetails);
            return ResponseEntity.ok(updatedAppointment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error de validación: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    // Eliminar una cita
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.ok("Cita eliminada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    // Buscar citas por fecha
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDate(date);
        return ResponseEntity.ok(appointments);
    }

    // Buscar citas por estado
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getAppointmentsByStatus(@PathVariable String status) {
        try {
            Appointment.AppointmentStatus appointmentStatus = Appointment.AppointmentStatus.valueOf(status.toUpperCase());
            List<Appointment> appointments = appointmentService.getAppointmentsByStatus(appointmentStatus);
            return ResponseEntity.ok(appointments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Status no válido: " + status);
        }
    }

    // Buscar citas por fecha y estado
    @GetMapping("/date/{date}/status/{status}")
    public ResponseEntity<?> getAppointmentsByDateAndStatus(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String status) {
        try {
            Appointment.AppointmentStatus appointmentStatus = Appointment.AppointmentStatus.valueOf(status.toUpperCase());
            List<Appointment> appointments = appointmentService.getAppointmentsByDateAndStatus(date, appointmentStatus);
            return ResponseEntity.ok(appointments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Status no válido: " + status);
        }
    }

    // Buscar citas en un rango de fechas
    @GetMapping("/date-range")
    public ResponseEntity<List<Appointment>> getAppointmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(appointments);
    }

    // Buscar citas por teléfono de contacto
    @GetMapping("/phone/{contactPhone}")
    public ResponseEntity<List<Appointment>> getAppointmentsByContactPhone(@PathVariable String contactPhone) {
        List<Appointment> appointments = appointmentService.getAppointmentsByContactPhone(contactPhone);
        return ResponseEntity.ok(appointments);
    }

    // Buscar citas por texto en resumen o descripción
    @GetMapping("/search")
    public ResponseEntity<List<Appointment>> searchAppointments(@RequestParam String q) {
        List<Appointment> appointments = appointmentService.searchAppointments(q);
        return ResponseEntity.ok(appointments);
    }

    // Cambiar estado de una cita
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeAppointmentStatus(
            @PathVariable Long id,
            @RequestParam String newStatus) {
        try {
            Appointment.AppointmentStatus status = Appointment.AppointmentStatus.valueOf(newStatus.toUpperCase());
            Appointment updatedAppointment = appointmentService.changeAppointmentStatus(id, status);
            return ResponseEntity.ok(updatedAppointment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Status no válido: " + newStatus);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    // Verificar disponibilidad en una fecha y hora específica
    @GetMapping("/availability")
    public ResponseEntity<Boolean> checkAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) String startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) String endTime,
            @RequestParam(required = false) Long excludeAppointmentId) {
        
        try {
            java.time.LocalTime start = java.time.LocalTime.parse(startTime);
            java.time.LocalTime end = java.time.LocalTime.parse(endTime);
            
            boolean isAvailable = appointmentService.isTimeSlotAvailable(date, start, end, excludeAppointmentId);
            return ResponseEntity.ok(isAvailable);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(false);
        }
    }
}
