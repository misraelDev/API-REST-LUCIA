package com.lucia.service;

import com.lucia.dto.AuthResponse;
import com.lucia.dto.LoginRequest;
import com.lucia.dto.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final SupabaseAuthService supabaseAuthService;

    public AuthService(SupabaseAuthService supabaseAuthService) {
        this.supabaseAuthService = supabaseAuthService;
    }

    public AuthResponse register(RegisterRequest request) {
        return supabaseAuthService.signUp(request);
    }

    public AuthResponse login(LoginRequest request) {
        return supabaseAuthService.signIn(request);
    }

    public AuthResponse refreshToken(String refreshToken) {
        return supabaseAuthService.refreshToken(refreshToken);
    }

    public AuthResponse confirmEmail(String token, String email) {
        return supabaseAuthService.confirmEmail(token, email);
    }

    public void resendConfirmationEmail(String email) {
        supabaseAuthService.resendConfirmationEmail(email);
    }

    public AuthResponse verifyUserToken(String accessToken) {
        return supabaseAuthService.verifyUserToken(accessToken);
    }

    public void logout() {
        System.out.println("User logged out");
    }
}


