package com.lucia.dto;

import jakarta.validation.constraints.NotBlank;

public class ReferralRequest {

    @NotBlank(message = "El ID del seller es requerido")
    private String sellerId;

    // Getters y Setters
    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }
}
