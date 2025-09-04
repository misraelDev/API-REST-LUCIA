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

    public Profile getProfileById(UUID userId) {
        return profileRepository.findById(userId).orElse(null);
    }

    public java.util.List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    @Transactional
    public Profile updateProfile(UUID userId, String newRole) {
        Profile profile = profileRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));
        
        Profile.Role role = parseRole(newRole);
        Profile.Role oldRole = profile.getRole();
        
        // Si cambió de buyer/user a seller, crear referido automáticamente
        if ((oldRole == Profile.Role.buyer || oldRole == Profile.Role.user) && role == Profile.Role.seller) {
            try {
                ReferralRequest request = new ReferralRequest();
                request.setSellerId(userId.toString());
                
                var referral = referralService.createReferral(request);
                logger.info("Referral created automatically for user upgraded to seller: {}", referral.getReferralCode());
            } catch (Exception e) {
                logger.error("Failed to create automatic referral for upgraded seller: {}", userId, e);
            }
        }
        
        profile.setRole(role);
        Profile saved = profileRepository.save(profile);
        logger.info("Profile updated for user {}: {} -> {}", userId, oldRole, saved.getRole());
        
        return saved;
    }

    @Transactional
    public boolean deleteProfile(UUID userId) {
        Profile profile = profileRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));
        
        profileRepository.delete(profile);
        logger.info("Profile deleted for user: {}", userId);
        
        return true;
    }

    private Profile.Role parseRole(String role) {
        if (role == null || role.isBlank()) return Profile.Role.user;
        String normalized = role.toLowerCase(Locale.ROOT);
        try {
            return Profile.Role.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            logger.warn("Invalid role '{}' provided, defaulting to 'user'", role);
            return Profile.Role.user;
        }
    }
}


