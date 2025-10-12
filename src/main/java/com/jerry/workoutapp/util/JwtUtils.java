package com.jerry.workoutapp.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT Utility Class
 * Handles JWT token generation, validation, and information extraction
 * Used for stateless authentication in the application
 */
@Component
public class JwtUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);

    // JWT signing secret - loaded from application.properties
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    // Token expiration time in ms - loaded from application.properties
    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Generate JWT token from authenticated user
     * Creates a signed token containing username and expiration time
     */
    public String generateJwtToken(Authentication authentication) {
        // Get user details from authentication object
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        // Build JWT token with user info and timing
        return Jwts.builder().setSubject(userPrincipal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Extract username from JWT token
     * Parses token and returns the subject (username)
     */
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)  // Use secret to verify signature
                .parseClaimsJws(token)     // Parse and validate token
                .getBody()                 // Get claims body
                .getSubject();             // Extract subject (email)
    }

    /**
     * Extract expiration date from JWT token
     * Used for determining how long to blacklist tokens on logout
     */
    public Date getExpirationFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration(); // Extract expiration date
    }

    /**
     * Validate JWT token integrity and expiration
     * Logs specific error types for debugging
     */
    public boolean validateJwtToken(String authToken) {
        try {
            // Parse token with secret - will throw exception if invalid
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            LOGGER.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
