package com.lucia.repository;

import com.lucia.entity.Call;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CallRepository extends JpaRepository<Call, Long> {

    // Buscar llamadas por contacto
    List<Call> findByContactId(Long contactId);

    // Buscar llamadas por fecha
    List<Call> findByDate(LocalDateTime date);

    // Buscar llamadas en un rango de fechas
    List<Call> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Buscar llamadas por motivo
    List<Call> findByMotiveContainingIgnoreCase(String motive);

    // Buscar llamadas por intención
    List<Call> findByIntentContainingIgnoreCase(String intent);

    // Buscar llamadas por duración mínima
    List<Call> findByDurationGreaterThanEqual(Integer minDuration);

    // Buscar llamadas por duración máxima
    List<Call> findByDurationLessThanEqual(Integer maxDuration);

    // Buscar llamadas por rango de duración
    List<Call> findByDurationBetween(Integer minDuration, Integer maxDuration);

    // Buscar llamadas que tengan audio
    List<Call> findByAudioCombinedIsNotNull();

    // Buscar llamadas que tengan transcripción
    List<Call> findByMessagesIsNotNull();

    // Buscar llamadas por resumen o motivo
    List<Call> findBySummaryContainingIgnoreCaseOrMotiveContainingIgnoreCase(String summary, String motive);

    // Buscar llamadas por contacto y fecha
    List<Call> findByContactIdAndDate(Long contactId, LocalDateTime date);

    // Buscar llamadas por contacto en un rango de fechas
    List<Call> findByContactIdAndDateBetween(Long contactId, LocalDateTime startDate, LocalDateTime endDate);

    // Contar llamadas por contacto
    Long countByContactId(Long contactId);

    // Obtener duración total de llamadas por contacto
    @Query("SELECT COALESCE(SUM(c.duration), 0) FROM Call c WHERE c.contact.id = :contactId")
    Long getTotalDurationByContactId(@Param("contactId") Long contactId);

    // Obtener llamadas más recientes por contacto
    List<Call> findTop5ByContactIdOrderByDateDesc(Long contactId);

    // Obtener llamadas más largas
    List<Call> findTop10ByOrderByDurationDesc();

    // Buscar llamadas por texto en resumen, motivo o intención
    @Query("SELECT c FROM Call c WHERE " +
           "LOWER(c.summary) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.motive) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.intent) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Call> findBySearchTerm(@Param("searchTerm") String searchTerm);

    // Obtener estadísticas de llamadas por contacto
    @Query("SELECT c.contact.id, COUNT(c), COALESCE(SUM(c.duration), 0), AVG(c.duration) " +
           "FROM Call c WHERE c.contact IS NOT NULL GROUP BY c.contact.id")
    List<Object[]> getCallStatsByContact();

    // Obtener llamadas del día actual
    @Query("SELECT c FROM Call c WHERE c.date >= :startOfDay AND c.date < :endOfDay")
    List<Call> findTodayCalls(@Param("startOfDay") LocalDateTime startOfDay, 
                              @Param("endOfDay") LocalDateTime endOfDay);

    // Obtener llamadas de la semana actual
    @Query("SELECT c FROM Call c WHERE c.date >= :startOfWeek AND c.date <= :endOfWeek")
    List<Call> findThisWeekCalls(@Param("startOfWeek") LocalDateTime startOfWeek, 
                                 @Param("endOfWeek") LocalDateTime endOfWeek);

    // ===== MÉTODOS OPTIMIZADOS PARA STATS SERVICE =====

    // Obtener duración promedio de todas las llamadas
    @Query("SELECT AVG(c.duration) FROM Call c WHERE c.duration IS NOT NULL")
    Double findAverageDuration();

    // Obtener las 5 llamadas más recientes
    List<Call> findTop5ByOrderByDateDesc();

    // Obtener los motivos más frecuentes con su conteo
    @Query("SELECT c.motive, COUNT(c) FROM Call c WHERE c.motive IS NOT NULL AND c.motive != '' " +
           "GROUP BY c.motive ORDER BY COUNT(c) DESC")
    List<Object[]> findTopMotivesWithCount(@Param("limit") int limit);

    // Contar llamadas en un rango de fechas
    Long countByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Contar llamadas después de una fecha
    Long countByDateAfter(LocalDateTime date);
}
