package com.lucia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ReferralSaleRequest {

    @NotBlank(message = "El código de referido es requerido")
    private String referralCode;

    @NotBlank(message = "El email del cliente es requerido")
    @Email(message = "El formato del email no es válido")
    private String customerEmail;

    @NotBlank(message = "El nombre del cliente es requerido")
    private String customerName;

    @NotNull(message = "El monto de la venta es requerido")
    @Positive(message = "El monto de la venta debe ser positivo")
    private Double saleAmount;

    private String saleDescription;

    private String notes;

    // Getters y Setters
    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Double getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(Double saleAmount) {
        this.saleAmount = saleAmount;
    }

    public String getSaleDescription() {
        return saleDescription;
    }

    public void setSaleDescription(String saleDescription) {
        this.saleDescription = saleDescription;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
