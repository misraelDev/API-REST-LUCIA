package com.lucia.repository;

import com.lucia.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    // Buscar contacto por nombre
    List<Contact> findByNameContainingIgnoreCase(String name);

    // Buscar contacto por email
    Optional<Contact> findByEmail(String email);

    // Buscar contacto por número de teléfono
    Optional<Contact> findByPhoneNumber(String phoneNumber);

    // Buscar contactos que contengan un texto en nombre o email
    @Query("SELECT c FROM Contact c WHERE c.name LIKE %:searchTerm% OR c.email LIKE %:searchTerm%")
    List<Contact> findByNameOrEmailContaining(@Param("searchTerm") String searchTerm);

    // Verificar si existe un email
    boolean existsByEmail(String email);

    // Verificar si existe un número de teléfono
    boolean existsByPhoneNumber(String phoneNumber);

    // ===== MÉTODOS OPTIMIZADOS PARA STATS SERVICE =====

    // Obtener los 5 contactos más recientes
    List<Contact> findTop5ByOrderByCreatedAtDesc();
}
