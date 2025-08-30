package com.lucia.controller;

import com.lucia.dto.ReferralRequest;
import com.lucia.dto.ReferralResponse;
import com.lucia.service.ReferralService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/referrals")
@CrossOrigin(origins = "*")
public class ReferralController {

    private static final Logger logger = LoggerFactory.getLogger(ReferralController.class);

    private final ReferralService referralService;

    public ReferralController(ReferralService referralService) {
        this.referralService = referralService;
    }

    /**
     * Crea un nuevo referido para un seller
     */
    @PostMapping
    public ResponseEntity<ReferralResponse> createReferral(@Valid @RequestBody ReferralRequest request) {
        try {
            logger.info("Creating referral for seller: {}", request.getSellerId());
            
            ReferralResponse referral = referralService.createReferral(request);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(referral);
            
        } catch (Exception e) {
            logger.error("Failed to create referral for seller: {}", request.getSellerId(), e);
            throw e;
        }
    }

    /**
     * Obtiene el referido activo de un seller
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<ReferralResponse> getReferralBySellerId(@PathVariable String sellerId) {
        try {
            logger.info("Getting referral for seller: {}", sellerId);
            
            ReferralResponse referral = referralService.getReferralBySellerId(sellerId);
            
            return ResponseEntity.ok(referral);
            
        } catch (Exception e) {
            logger.error("Failed to get referral for seller: {}", sellerId, e);
            throw e;
        }
    }

    /**
     * Obtiene todos los referidos de un seller
     */
    @GetMapping("/seller/{sellerId}/all")
    public ResponseEntity<List<ReferralResponse>> getAllReferralsBySellerId(@PathVariable String sellerId) {
        try {
            logger.info("Getting all referrals for seller: {}", sellerId);
            
            List<ReferralResponse> referrals = referralService.getAllReferralsBySellerId(sellerId);
            
            return ResponseEntity.ok(referrals);
            
        } catch (Exception e) {
            logger.error("Failed to get all referrals for seller: {}", sellerId, e);
            throw e;
        }
    }

    /**
     * Valida un código de referido
     */
    @GetMapping("/validate/{referralCode}")
    public ResponseEntity<ReferralResponse> validateReferralCode(@PathVariable String referralCode) {
        try {
            logger.info("Validating referral code: {}", referralCode);
            
            var referral = referralService.validateReferralCode(referralCode);
            ReferralResponse response = new ReferralResponse(
                referral.getId(),
                referral.getSellerId(),
                referral.getReferralCode(),
                referral.getIsActive(),
                referral.getCreatedAt()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to validate referral code: {}", referralCode, e);
            throw e;
        }
    }

    /**
     * Desactiva un referido
     */
    @DeleteMapping("/{referralId}")
    public ResponseEntity<Void> deactivateReferral(@PathVariable Long referralId) {
        try {
            logger.info("Deactivating referral: {}", referralId);
            
            referralService.deactivateReferral(referralId);
            
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            logger.error("Failed to deactivate referral: {}", referralId, e);
            throw e;
        }
    }
}
