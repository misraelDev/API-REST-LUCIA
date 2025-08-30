package com.lucia.service;

import com.lucia.dto.ReferralRequest;
import com.lucia.dto.ReferralResponse;
import com.lucia.entity.Referral;
import com.lucia.entity.Profile;
import com.lucia.exception.AuthenticationException;
import com.lucia.repository.ReferralRepository;
import com.lucia.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReferralService {

    private static final Logger logger = LoggerFactory.getLogger(ReferralService.class);

    private final ReferralRepository referralRepository;
    private final ProfileRepository profileRepository;

    public ReferralService(ReferralRepository referralRepository,
                          ProfileRepository profileRepository) {
        this.referralRepository = referralRepository;
        this.profileRepository = profileRepository;
    }

    /**
     * Crea un nuevo referido para un seller
     */
    @Transactional
    public ReferralResponse createReferral(ReferralRequest request) {
        logger.info("Creating referral for seller ID: {}", request.getSellerId());

        // Validar que el seller exista y tenga rol seller
        try {
            var sellerUuid = java.util.UUID.fromString(request.getSellerId());
            Profile sellerProfile = profileRepository.findById(sellerUuid)
                    .orElseThrow(() -> new AuthenticationException("Seller no encontrado en profiles"));
            if (sellerProfile.getRole() != Profile.Role.seller) {
                throw new AuthenticationException("El usuario no tiene rol 'seller'");
            }
        } catch (IllegalArgumentException ex) {
            throw new AuthenticationException("sellerId no es un UUID válido");
        }

        // Verificar si ya existe un referido activo para este seller
        Optional<Referral> existingReferral = referralRepository.findActiveBySellerId(request.getSellerId());
        if (existingReferral.isPresent()) {
            logger.warn("Seller {} already has an active referral", request.getSellerId());
            throw new AuthenticationException("El seller ya tiene un referido activo");
        }

        // Crear nuevo referido
        Referral referral = new Referral();
        referral.setSellerId(request.getSellerId());

        Referral savedReferral = referralRepository.save(referral);
        logger.info("Referral created successfully for seller: {}", request.getSellerId());

        return mapToResponse(savedReferral);
    }

    /**
     * Obtiene el referido activo de un seller
     */
    public ReferralResponse getReferralBySellerId(String sellerId) {
        logger.info("Getting referral for seller ID: {}", sellerId);

        Optional<Referral> referral = referralRepository.findActiveBySellerId(sellerId);
        if (referral.isEmpty()) {
            throw new AuthenticationException("No se encontró un referido activo para este seller");
        }

        return mapToResponse(referral.get());
    }

    /**
     * Obtiene todos los referidos de un seller
     */
    public List<ReferralResponse> getAllReferralsBySellerId(String sellerId) {
        logger.info("Getting all referrals for seller ID: {}", sellerId);

        List<Referral> referrals = referralRepository.findBySellerId(sellerId);
        return referrals.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Valida un código de referido y retorna la información del seller
     */
    public Referral validateReferralCode(String referralCode) {
        logger.info("Validating referral code: {}", referralCode);

        Optional<Referral> referral = referralRepository.findActiveByReferralCode(referralCode);
        if (referral.isEmpty()) {
            throw new AuthenticationException("Código de referido inválido o inactivo");
        }

        return referral.get();
    }

    /**
     * Obtiene el sellerId de un código de referido
     * Retorna null si el código no existe o no es válido
     */
    public String getSellerIdFromReferralCode(String referralCode) {
        if (referralCode == null || referralCode.trim().isEmpty()) {
            logger.info("No referral code provided");
            return null;
        }
        
        logger.info("Validating referral code: {}", referralCode);
        
        // Buscar el referido activo
        Optional<Referral> referral = referralRepository.findByReferralCode(referralCode);
        if (referral.isEmpty()) {
            logger.warn("Referral code not found: {}", referralCode);
            return null;
        }
        
        Referral ref = referral.get();
        
        // Verificar que el referido esté activo
        if (!ref.getIsActive()) {
            logger.warn("Referral code is inactive: {}", referralCode);
            return null;
        }
        
        logger.info("Referral code validated for seller: {}", ref.getSellerId());
        return ref.getSellerId();
    }

    /**
     * Desactiva un referido
     */
    @Transactional
    public void deactivateReferral(Long referralId) {
        logger.info("Deactivating referral ID: {}", referralId);

        Optional<Referral> referral = referralRepository.findById(referralId);
        if (referral.isEmpty()) {
            throw new AuthenticationException("Referido no encontrado");
        }

        Referral ref = referral.get();
        ref.setIsActive(false);
        referralRepository.save(ref);

        logger.info("Referral deactivated successfully");
    }

    /**
     * Mapea la entidad Referral a ReferralResponse
     */
    private ReferralResponse mapToResponse(Referral referral) {
        return new ReferralResponse(
                referral.getId(),
                referral.getSellerId(),
                referral.getReferralCode(),
                referral.getIsActive(),
                referral.getCreatedAt()
        );
    }
}
