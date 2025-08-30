package com.lucia.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "referrals")
public class Referral extends BaseEntity {

    @Column(name = "seller_id", nullable = false)
    private String sellerId;

    @Column(name = "referral_code", unique = true, nullable = false)
    private String referralCode;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;



    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (referralCode == null) {
            referralCode = generateReferralCode();
        }
    }

    private String generateReferralCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Getters y Setters
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
