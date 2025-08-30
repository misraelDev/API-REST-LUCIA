package com.lucia.service;

import com.lucia.dto.RequestCreateDto;
import com.lucia.entity.Request;
import com.lucia.repository.RequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    private static final Logger logger = LoggerFactory.getLogger(RequestService.class);

    private final RequestRepository requestRepository;

    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    /**
     * Crea una nueva solicitud de contacto
     */
    @Transactional
    public Request createRequest(RequestCreateDto requestDto) {
        logger.info("Creating new contact request for: {}", requestDto.getEmail());

        Request request = new Request();
        request.setName(requestDto.getName());
        request.setEmail(requestDto.getEmail());
        request.setPhone(requestDto.getPhone());
        request.setNeed(requestDto.getNeed());
        request.setMessage(requestDto.getMessage());
        request.setReferralCode(requestDto.getReferralCode());
        request.setSellerId(requestDto.getSellerId());
        request.setStatus(Request.RequestStatus.PENDING);

        Request savedRequest = requestRepository.save(request);
        logger.info("Contact request created successfully with ID: {}", savedRequest.getId());



        return savedRequest;
    }

    /**
     * Obtiene todas las solicitudes
     */
    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    /**
     * Obtiene una solicitud por ID
     */
    public Optional<Request> getRequestById(Long id) {
        return requestRepository.findById(id);
    }

    /**
     * Obtiene solicitudes por código de referido
     */
    public List<Request> getRequestsByReferralCode(String referralCode) {
        return requestRepository.findByReferralCode(referralCode);
    }

    /**
     * Obtiene solicitudes por email
     */
    public List<Request> getRequestsByEmail(String email) {
        return requestRepository.findByEmail(email);
    }

    /**
     * Obtiene solicitudes por estado
     */
    public List<Request> getRequestsByStatus(Request.RequestStatus status) {
        return requestRepository.findByStatus(status);
    }

    /**
     * Actualiza el estado de una solicitud
     */
    @Transactional
    public Request updateRequestStatus(Long requestId, Request.RequestStatus newStatus) {
        Optional<Request> optionalRequest = requestRepository.findById(requestId);
        if (optionalRequest.isEmpty()) {
            throw new IllegalArgumentException("Request not found with ID: " + requestId);
        }

        Request request = optionalRequest.get();
        request.setStatus(newStatus);
        


        Request updatedRequest = requestRepository.save(request);
        logger.info("Request status updated to {} for ID: {}", newStatus, requestId);
        
        return updatedRequest;
    }

    

    /**
     * Obtiene estadísticas de solicitudes
     */
    public RequestStats getRequestStats() {
        List<Request> allRequests = requestRepository.findAll();
        
        long totalRequests = allRequests.size();
        long pendingRequests = allRequests.stream()
                .filter(r -> r.getStatus() == Request.RequestStatus.PENDING)
                .count();
        long completedRequests = allRequests.stream()
                .filter(r -> r.getStatus() == Request.RequestStatus.COMPLETED)
                .count();
        long requestsWithReferral = allRequests.stream()
                .filter(r -> r.getReferralCode() != null && !r.getReferralCode().trim().isEmpty())
                .count();

        return new RequestStats(totalRequests, pendingRequests, completedRequests, requestsWithReferral);
    }

    /**
     * Clase interna para estadísticas
     */
    public static class RequestStats {
        private final long totalRequests;
        private final long pendingRequests;
        private final long completedRequests;
        private final long requestsWithReferral;

        public RequestStats(long totalRequests, long pendingRequests, long completedRequests, long requestsWithReferral) {
            this.totalRequests = totalRequests;
            this.pendingRequests = pendingRequests;
            this.completedRequests = completedRequests;
            this.requestsWithReferral = requestsWithReferral;
        }

        // Getters
        public long getTotalRequests() { return totalRequests; }
        public long getPendingRequests() { return pendingRequests; }
        public long getCompletedRequests() { return completedRequests; }
        public long getRequestsWithReferral() { return requestsWithReferral; }
    }

    /**
     * Actualiza una solicitud existente
     */
    @Transactional
    public Request updateRequest(Request request) {
        logger.info("Updating request with ID: {}", request.getId());
        
        Request updatedRequest = requestRepository.save(request);
        logger.info("Request updated successfully with ID: {}", updatedRequest.getId());
        return updatedRequest;
    }
}
