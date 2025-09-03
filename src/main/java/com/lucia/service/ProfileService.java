package com.lucia.service;

import com.lucia.entity.Profile;
import com.lucia.repository.ProfileRepository;
import com.lucia.dto.ReferralRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);

    private final ProfileRepository profileRepository;
    private final ReferralService referralService;

    public ProfileService(ProfileRepository profileRepository, ReferralService referralService) {
        this.profileRepository = profileRepository;
        this.referralService = referralService;
    }

    @Transactional
    public Profile upsertProfile(UUID userId, String roleNullable) {
        Profile.Role role = parseRole(roleNullable);

        Profile profile = profileRepository.findById(userId).orElseGet(() -> {
            Profile p = new Profile();
            p.setId(userId);
            return p;
        });
        
        boolean isNewProfile = profile.getRole() == null;
        profile.setRole(role);
        Profile saved = profileRepository.save(profile);
        logger.info("Profile saved for user {} with role {}", userId, saved.getRole());
        
        // Si es un perfil nuevo o si cambió a seller, crear referido
        if (isNewProfile && role == Profile.Role.seller) {
            try {
                ReferralRequest request = new ReferralRequest();
                request.setSellerId(userId.toString());
                
                var referral = referralService.createReferral(request);
                logger.info("Referral created automatically for new seller: {}", referral.getReferralCode());
            } catch (Exception e) {
                logger.error("Failed to create automatic referral for seller: {}", userId, e);
            }
        }
        
        return saved;
    }

    private Profile.Role parseRole(String role) {
        if (role == null || role.isBlank()) return Profile.Role.buyer;
        String normalized = role.toLowerCase(Locale.ROOT);
        // Mapear explícitamente "user" a un rol permitido por la BD
        if ("user".equals(normalized)) {
            logger.info("Mapping role 'user' to 'buyer' to satisfy DB check constraint");
            return Profile.Role.buyer;
        }
        try {
            return Profile.Role.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return Profile.Role.buyer;
        }
    }
}


