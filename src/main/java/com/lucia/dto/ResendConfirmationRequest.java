package com.lucia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ResendConfirmationRequest {

    @JsonProperty("email")
    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email no es válido")
    private String email;

    public ResendConfirmationRequest() {}

    public ResendConfirmationRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
