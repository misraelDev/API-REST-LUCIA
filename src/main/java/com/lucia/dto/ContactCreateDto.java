package com.lucia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ContactCreateDto {

    @NotBlank(message = "El nombre del contacto es obligatorio")
    private String name;

    @Email(message = "El formato del email no es válido")
    private String email;

    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]{7,20}$", 
             message = "El formato del teléfono no es válido")
    private String phoneNumber;

    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
