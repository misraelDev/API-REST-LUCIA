package com.lucia.repository;

import com.lucia.entity.Referral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {

    Optional<Referral> findByReferralCode(String referralCode);
    
    List<Referral> findBySellerId(String sellerId);
    
    List<Referral> findByIsActiveTrue();
    
    @Query("SELECT r FROM Referral r WHERE r.sellerId = :sellerId AND r.isActive = true")
    Optional<Referral> findActiveBySellerId(@Param("sellerId") String sellerId);
    
    @Query("SELECT r FROM Referral r WHERE r.referralCode = :referralCode AND r.isActive = true")
    Optional<Referral> findActiveByReferralCode(@Param("referralCode") String referralCode);
    
    boolean existsByReferralCode(String referralCode);
}
