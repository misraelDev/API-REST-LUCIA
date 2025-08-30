package com.lucia.dto;

import java.time.LocalDateTime;

public class ReferralResponse {

    private Long id;
    private String sellerId;
    private String referralCode;
    private Boolean isActive;
    private LocalDateTime createdAt;


    // Constructor
    public ReferralResponse() {}

    public ReferralResponse(Long id, String sellerId, String referralCode, 
                           Boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.sellerId = sellerId;
        this.referralCode = referralCode;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


}
