package com.jerry.workoutapp.config;

import com.jerry.workoutapp.service.JwtBlacklistService;
import com.jerry.workoutapp.service.UserDetailsServiceImpl;
import com.jerry.workoutapp.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.jerry.workoutapp.util.SecurityConstants.AUTH_HEADER;
import static com.jerry.workoutapp.util.SecurityConstants.TOKEN_PREFIX;

/**
 * JWT Authentication Filter - runs on every HTTP request
 * Extracts and validates JWT tokens from Authorization header
 * Sets up Spring Security context if token is valid
 */
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    JwtBlacklistService jwtBlacklistService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Extract JWT token from Authorization header
            String jwt = parseJwt(request);

            // Check if token exists, is valid, and not blacklisted
            if (jwt != null && jwtUtils.validateJwtToken(jwt) && !jwtBlacklistService.isTokenBlacklisted(jwt)) {
                // Get username from token and load user details
                String username = jwtUtils.getUsernameFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Create authentication object and set in security context
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Log authentication errors
            LOGGER.error("Cannot set user auth {}", e.getMessage());
        }

        // Continue with the filter chain regardless of authentication result
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     * Expected format: "Bearer <token>"
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(AUTH_HEADER);

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(TOKEN_PREFIX)) {
            return headerAuth.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}
