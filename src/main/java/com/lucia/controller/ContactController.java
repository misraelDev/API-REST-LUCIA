package com.lucia.controller;

import com.lucia.entity.Contact;
import com.lucia.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = "*")
public class ContactController {

    @Autowired
    private ContactService contactService;

    // Crear un nuevo contacto
    @PostMapping
    public ResponseEntity<?> createContact(@RequestBody Contact contact) {
        try {
            Contact createdContact = contactService.createContact(contact);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdContact);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    // Obtener todos los contactos
    @GetMapping
    public ResponseEntity<List<Contact>> getAllContacts() {
        List<Contact> contacts = contactService.getAllContacts();
        return ResponseEntity.ok(contacts);
    }

    // Obtener un contacto por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getContactById(@PathVariable Long id) {
        Optional<Contact> contact = contactService.getContactById(id);
        
        if (contact.isPresent()) {
            return ResponseEntity.ok(contact.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Actualizar un contacto
    @PutMapping("/{id}")
    public ResponseEntity<?> updateContact(@PathVariable Long id, @RequestBody Contact contactDetails) {
        try {
            Contact updatedContact = contactService.updateContact(id, contactDetails);
            return ResponseEntity.ok(updatedContact);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error de validación: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    // Eliminar un contacto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContact(@PathVariable Long id) {
        try {
            contactService.deleteContact(id);
            return ResponseEntity.ok("Contacto eliminado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor: " + e.getMessage());
        }
    }

    // Buscar contactos por nombre
    @GetMapping("/search/name/{name}")
    public ResponseEntity<List<Contact>> getContactsByName(@PathVariable String name) {
        List<Contact> contacts = contactService.getContactsByName(name);
        return ResponseEntity.ok(contacts);
    }

    // Buscar contacto por email
    @GetMapping("/search/email/{email}")
    public ResponseEntity<?> getContactByEmail(@PathVariable String email) {
        Optional<Contact> contact = contactService.getContactByEmail(email);
        
        if (contact.isPresent()) {
            return ResponseEntity.ok(contact.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Buscar contacto por número de teléfono
    @GetMapping("/search/phone/{phoneNumber}")
    public ResponseEntity<?> getContactByPhoneNumber(@PathVariable String phoneNumber) {
        Optional<Contact> contact = contactService.getContactByPhoneNumber(phoneNumber);
        
        if (contact.isPresent()) {
            return ResponseEntity.ok(contact.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Buscar contactos por texto en nombre o email
    @GetMapping("/search")
    public ResponseEntity<List<Contact>> searchContacts(@RequestParam String q) {
        List<Contact> contacts = contactService.searchContacts(q);
        return ResponseEntity.ok(contacts);
    }

    // Verificar si existe un email
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = contactService.emailExists(email);
        return ResponseEntity.ok(exists);
    }

    // Verificar si existe un número de teléfono
    @GetMapping("/exists/phone/{phoneNumber}")
    public ResponseEntity<Boolean> checkPhoneNumberExists(@PathVariable String phoneNumber) {
        boolean exists = contactService.phoneNumberExists(phoneNumber);
        return ResponseEntity.ok(exists);
    }
}
