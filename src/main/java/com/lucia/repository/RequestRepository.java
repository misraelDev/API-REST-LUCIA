package com.lucia.repository;

import com.lucia.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByReferralCode(String referralCode);
    
    List<Request> findByEmail(String email);
    
    List<Request> findByStatus(Request.RequestStatus status);
    
    @Query("SELECT r FROM Request r WHERE r.submittedAt BETWEEN :startDate AND :endDate")
    List<Request> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT r FROM Request r WHERE r.referralCode IS NOT NULL")
    List<Request> findRequestsWithReferral();
    
    @Query("SELECT COUNT(r) FROM Request r WHERE r.referralCode = :referralCode")
    Long countByReferralCode(@Param("referralCode") String referralCode);
}
