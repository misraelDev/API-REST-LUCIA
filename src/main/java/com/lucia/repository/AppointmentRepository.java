package com.lucia.repository;

import com.lucia.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Buscar citas por fecha
    List<Appointment> findByDate(LocalDate date);

    // Buscar citas por estado
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);

    // Buscar citas por fecha y estado
    List<Appointment> findByDateAndStatus(LocalDate date, Appointment.AppointmentStatus status);

    // Buscar citas en un rango de fechas
    @Query("SELECT a FROM Appointment a WHERE a.date BETWEEN :startDate AND :endDate")
    List<Appointment> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Buscar citas por teléfono de contacto
    List<Appointment> findByContactPhone(String contactPhone);

    // Buscar citas que contengan un texto en el resumen o descripción
    @Query("SELECT a FROM Appointment a WHERE a.summary LIKE %:searchTerm% OR a.description LIKE %:searchTerm%")
    List<Appointment> findBySummaryOrDescriptionContaining(@Param("searchTerm") String searchTerm);

    // ===== MÉTODOS OPTIMIZADOS PARA STATS SERVICE =====

    // Contar citas por fecha específica
    Long countByDate(LocalDate date);

    // Contar citas por año y mes
    @Query("SELECT COUNT(a) FROM Appointment a WHERE YEAR(a.date) = :year AND MONTH(a.date) = :month")
    Long countByYearAndMonth(@Param("year") int year, @Param("month") int month);

    // Contar citas después de una fecha
    Long countByDateAfter(LocalDate date);
}
