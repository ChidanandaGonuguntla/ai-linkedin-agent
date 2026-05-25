package com.dataforge.ailinkedinagent.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.security.api-token}")
    private String apiToken;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/api/linkedin/oauth/callback").permitAll()
                .anyRequest().permitAll()
            )
            .addFilterBefore(new ApiTokenFilter(apiToken), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    static class ApiTokenFilter extends OncePerRequestFilter {
        private final String apiToken;
        ApiTokenFilter(String apiToken) { this.apiToken = apiToken; }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            String path = request.getRequestURI();
            if (!path.startsWith("/api/") || path.equals("/api/linkedin/oauth/callback")) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = request.getHeader("X-API-TOKEN");
            if (apiToken == null || apiToken.isBlank() || apiToken.equals(token)) {
                filterChain.doFilter(request, response);
                return;
            }
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Missing or invalid X-API-TOKEN");
        }
    }
}
