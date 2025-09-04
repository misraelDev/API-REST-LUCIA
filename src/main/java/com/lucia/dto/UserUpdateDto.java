package com.lucia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public class UserUpdateDto {
    
    @Email(message = "El email debe tener un formato válido")
    private String email;
    
    private String fullName;
    
    @Pattern(regexp = "^(user|buyer|seller|admin)$", message = "El rol debe ser: user, buyer, seller o admin")
    private String role;
    
    // Constructores
    public UserUpdateDto() {}
    
    public UserUpdateDto(String email, String fullName, String role) {
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }
    
    // Getters y Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    // Método para verificar si hay algún campo para actualizar
    public boolean hasUpdates() {
        return email != null || fullName != null || role != null;
    }
}
