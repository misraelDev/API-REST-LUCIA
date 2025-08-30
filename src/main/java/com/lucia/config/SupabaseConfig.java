package com.lucia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SupabaseConfig {

    @Value("${supabase.url:}")
    private String supabaseUrl;

    @Value("${supabase.anon-key:}")
    private String anonKey;

    @Value("${supabase.service-role-key:}")
    private String serviceRoleKey;

    @Value("${supabase.jwt-secret:}")
    private String jwtSecret;

    @Bean
    public WebClient supabaseWebClient() {
        return WebClient.builder()
                .baseUrl(supabaseUrl)
                .defaultHeader("apikey", anonKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String getSupabaseUrl() {
        return supabaseUrl;
    }

    public String getAnonKey() {
        return anonKey;
    }

    public String getServiceRoleKey() {
        return serviceRoleKey;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }
}


