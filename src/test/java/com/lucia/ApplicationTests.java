package com.lucia;

import com.lucia.config.SupabaseConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private SupabaseConfig supabaseConfig;

    @Test
    void contextLoads() {
    }

    @Test
    void supabaseConfigLoaded() {
        assertNotNull(supabaseConfig);
        assertNotNull(supabaseConfig.getSupabaseUrl());
        assertNotNull(supabaseConfig.getAnonKey());
        assertFalse(supabaseConfig.getSupabaseUrl().isEmpty());
        assertFalse(supabaseConfig.getAnonKey().isEmpty());
        
        System.out.println("Supabase URL: " + supabaseConfig.getSupabaseUrl());
        System.out.println("Supabase Anon Key: " + supabaseConfig.getAnonKey().substring(0, 20) + "...");
    }
}
