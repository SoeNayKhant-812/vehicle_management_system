package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/cars/**").permitAll() // Allow all requests to /cars/**
                        .anyRequest().authenticated()            // Protect other routes
                )
                .csrf(AbstractHttpConfigurer::disable) // ✅ Modern disabling syntax (lambda form)

                // Optionally, customize the login form if needed
                .httpBasic(Customizer.withDefaults()); // or .formLogin(Customizer.withDefaults());

        return http.build();
    }
}
