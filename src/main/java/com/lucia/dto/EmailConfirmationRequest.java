package com.lucia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class EmailConfirmationRequest {

    @JsonProperty("token")
    @NotBlank(message = "El token de confirmación es requerido")
    private String token;

    public EmailConfirmationRequest() {}

    public EmailConfirmationRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
