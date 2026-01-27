package com.example.gateway_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
                http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .authorizeExchange(exchanges -> exchanges
                                                // Rutas públicas (sin autenticación)
                                                .pathMatchers("/actuator/**").permitAll()
                                                .pathMatchers("/eureka/**").permitAll()
                                                .pathMatchers("/uploads/**").permitAll()
                                                // Todas las demás rutas requieren autenticación
                                                .anyExchange().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> {})
                                );

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of(
                                "http://localhost:5173", // Vite dev server
                                "http://localhost:3000", // Alternative dev port
                                "http://127.0.0.1:5173",
                                "http://127.0.0.1:3000",
                                "http://192.168.49.2:30080", // Minikube Docker driver
                                "http://192.168.39.241:30080", // Minikube KVM2 driver actual
                                "http://192.168.39.241:30000" // Gateway port desde frontend
                ));
                configuration.setAllowedOriginPatterns(List.of(
                                "http://192.168.*:*", // Cualquier IP de red local Minikube
                                "http://127.0.0.1:*" // Cualquier puerto en localhost
                ));
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
