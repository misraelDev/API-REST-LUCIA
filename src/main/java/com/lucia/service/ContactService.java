package com.lucia.service;

import com.lucia.entity.Contact;
import com.lucia.repository.ContactRepository;
import com.lucia.service.WebSocketNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private WebSocketNotificationService webSocketNotificationService;

    // Crear un nuevo contacto
    public Contact createContact(Contact contact) {
        // Validar que el email sea único si se proporciona
        if (contact.getEmail() != null && contactRepository.existsByEmail(contact.getEmail())) {
            throw new IllegalArgumentException("Ya existe un contacto con este email: " + contact.getEmail());
        }
        
        // Validar que el teléfono sea único si se proporciona
        if (contact.getPhoneNumber() != null && contactRepository.existsByPhoneNumber(contact.getPhoneNumber())) {
            throw new IllegalArgumentException("Ya existe un contacto con este teléfono: " + contact.getPhoneNumber());
        }
        
        Contact savedContact = contactRepository.save(contact);
        
        // Notificar vía WebSocket
        webSocketNotificationService.notifyNewContact(savedContact);
        
        return savedContact;
    }

    // Obtener todos los contactos
    public List<Contact> getAllContacts() {
        List<Contact> contacts = contactRepository.findAll();
        
        // Notificar vía WebSocket sobre la consulta
        webSocketNotificationService.notifyContactsConsulted(contacts);
        
        return contacts;
    }

    // Obtener un contacto por ID
    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    // Actualizar un contacto
    public Contact updateContact(Long id, Contact contactDetails) {
        Optional<Contact> optionalContact = contactRepository.findById(id);
        
        if (optionalContact.isPresent()) {
            Contact existingContact = optionalContact.get();
            
            // Validar que el email sea único si se está cambiando
            if (contactDetails.getEmail() != null && !contactDetails.getEmail().equals(existingContact.getEmail())) {
                if (contactRepository.existsByEmail(contactDetails.getEmail())) {
                    throw new IllegalArgumentException("Ya existe un contacto con este email: " + contactDetails.getEmail());
                }
            }
            
            // Validar que el teléfono sea único si se está cambiando
            if (contactDetails.getPhoneNumber() != null && !contactDetails.getPhoneNumber().equals(existingContact.getPhoneNumber())) {
                if (contactRepository.existsByPhoneNumber(contactDetails.getPhoneNumber())) {
                    throw new IllegalArgumentException("Ya existe un contacto con este teléfono: " + contactDetails.getPhoneNumber());
                }
            }
            
            // Actualizar campos si no son null
            if (contactDetails.getName() != null) {
                existingContact.setName(contactDetails.getName());
            }
            if (contactDetails.getEmail() != null) {
                existingContact.setEmail(contactDetails.getEmail());
            }
            if (contactDetails.getPhoneNumber() != null) {
                existingContact.setPhoneNumber(contactDetails.getPhoneNumber());
            }
            
            Contact updatedContact = contactRepository.save(existingContact);
            
            // Notificar vía WebSocket
            webSocketNotificationService.notifyContactUpdated(updatedContact);
            
            return updatedContact;
        } else {
            throw new RuntimeException("Contacto no encontrado con ID: " + id);
        }
    }

    // Eliminar un contacto
    public void deleteContact(Long id) {
        Optional<Contact> optionalContact = contactRepository.findById(id);
        if (optionalContact.isPresent()) {
            Contact contact = optionalContact.get();
            contactRepository.deleteById(id);
            
            // Notificar vía WebSocket
            webSocketNotificationService.notifyContactDeleted(contact);
        } else {
            throw new RuntimeException("Contacto no encontrado con ID: " + id);
        }
    }

    // Buscar contactos por nombre
    public List<Contact> getContactsByName(String name) {
        return contactRepository.findByNameContainingIgnoreCase(name);
    }

    // Buscar contacto por email
    public Optional<Contact> getContactByEmail(String email) {
        return contactRepository.findByEmail(email);
    }

    // Buscar contacto por número de teléfono
    public Optional<Contact> getContactByPhoneNumber(String phoneNumber) {
        return contactRepository.findByPhoneNumber(phoneNumber);
    }

    // Buscar contactos por texto en nombre o email
    public List<Contact> searchContacts(String searchTerm) {
        return contactRepository.findByNameOrEmailContaining(searchTerm);
    }

    // Verificar si existe un email
    public boolean emailExists(String email) {
        return contactRepository.existsByEmail(email);
    }

    // Verificar si existe un número de teléfono
    public boolean phoneNumberExists(String phoneNumber) {
        return contactRepository.existsByPhoneNumber(phoneNumber);
    }
}
