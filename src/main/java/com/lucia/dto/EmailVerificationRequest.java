package com.lucia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailVerificationRequest {

    @JsonProperty("email")
    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email no es válido")
    private String email;

    @JsonProperty("token")
    @NotBlank(message = "El token de verificación es requerido")
    private String token;

    public EmailVerificationRequest() {}

    public EmailVerificationRequest(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
