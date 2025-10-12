package com.jerry.workoutapp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

/**
 * Custom handler for 403 Forbidden errors
 * Triggered when authenticated users try to access resources they don't have permission for
 */
@Component
public class AuthAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException {

        // Log the forbidden access attempt for security monitoring
        LOGGER.warn("Forbidden access attempt at {} by IP {} to {}", Instant.now(),
                request.getRemoteAddr(), request.getRequestURI());

        // Return structured JSON error response with 403 status
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write("{\"timestamp\":\"" + Instant.now() + "\",\"status\":403,\"error\":\"Forbidden\",\"path\":\"" + request.getRequestURI() + "\"}");
    }
}
