package com.monocept.myapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.monocept.myapp.security.JwtAuthenticationEntryPoint;
import com.monocept.myapp.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${FRONTEND_URL}")
    private String frontendUrl; // Load the frontend URL from environment variables

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAuthenticationFilter authenticationFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint authenticationEntryPoint,
                          JwtAuthenticationFilter authenticationFilter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authenticationFilter = authenticationFilter;
    }

    /**
     * Password encoder bean to hash passwords securely
     */
    @Bean
    static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager bean for managing user authentication
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Security filter chain configuration for HTTP security
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable()) // Disable CSRF protection for APIs
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS with custom configuration
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/trysol/auth/").permitAll() // Allow all auth-related endpoints
                .requestMatchers("/swagger-ui/", "/v3/api-docs/").permitAll() // Allow Swagger documentation endpoints
                .anyRequest().authenticated() // Restrict all other endpoints
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint) // Handle unauthorized access exceptions
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Set session management to stateless
            );

        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter before other filters

        return http.build();
    }

    /**
     * CORS configuration source for allowing requests from the frontend
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin(frontendUrl); // Allow only the frontend's origin
        config.addAllowedMethod("*"); // Allow all HTTP methods
        config.addAllowedHeader("*"); // Allow all headers
        config.setAllowCredentials(true); // Allow credentials (e.g., cookies)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/", config); // Apply this configuration to all endpoints
        return source;
    }
}