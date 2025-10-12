package com.jerry.workoutapp.controller;

import com.jerry.workoutapp.entity.LoginRequest;
import com.jerry.workoutapp.entity.SignUpRequest;
import com.jerry.workoutapp.entity.User;
import com.jerry.workoutapp.repository.UserRepository;
import com.jerry.workoutapp.service.JwtBlacklistService;
import com.jerry.workoutapp.entity.JwtResponse;
import com.jerry.workoutapp.util.JwtUtils;
import com.jerry.workoutapp.util.Validation;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.jerry.workoutapp.util.SecurityConstants.TOKEN_PREFIX;

/**
 * Authentication Controller - handles user registration, login, and logout
 * Provides REST endpoints for authentication operations
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private JwtBlacklistService jwtBlacklistService;
    @Autowired
    private Validation validation;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    // Register a new user account
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        // Validate password strength and email pattern using custom validation
        try {
            validation.validateEmail(signUpRequest.getEmail());
            validation.validatePassword(signUpRequest.getPassword());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }

        // Check if user already exists by email
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            LOGGER.info("Registration failed");
            return ResponseEntity.badRequest().body("Error: user already exists");
        }

        // Create new user with default values and encoded password
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(signUpRequest.getPassword())); // Hash password before storing
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    /**
     * Authenticate user and generate JWT token
     * Returns JWT token and user details on successful authentication
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            // Attempt to authenticate user with provided credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Generate JWT token for authenticated user
            String jwt = jwtUtils.generateJwtToken(authentication);

            // Log successful login
            LOGGER.info("LOGIN_SUCCESS: user={}, ip={}, path={}", loginRequest.getEmail(), request.getRemoteAddr(), request.getRequestURI());

            // Extract user details and roles from authentication object
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities()
                    .stream()
                    .map(item -> item.getAuthority()) // Convert authorities to string list
                    .collect(Collectors.toList());

            // Return JWT token and user information
            return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), roles));

        } catch (Exception e) {
            // Log failed login attempt for security monitoring
            LOGGER.warn("LOGIN_FAILED: user={}, ip={}, path={}", loginRequest.getEmail(), request.getRemoteAddr(), request.getRequestURI());
            throw e; // Re-throw to be handled by AuthEntryPointJwt (returns 401)
        }
    }

    /**
     * Logout user by blacklisting their JWT token
     * Prevents the token from being used for future requests
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader("Authorization") String authHeader, HttpServletRequest request) {
        // Validate Authorization header format
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            return ResponseEntity.badRequest().body("Error: Missing or invalid Authorization header");
        }

        // Extract JWT token from header (remove "Bearer " prefix)
        String token = authHeader.substring(7);
        // Get token expiration to know how long to blacklist it
        Date exp = jwtUtils.getExpirationFromJwtToken(token);

        // Add token to blacklist to prevent future use
        jwtBlacklistService.blacklistToken(token, exp);

        // Log logout
        LOGGER.info("LOGOUT: ip={}, path={}", request.getRemoteAddr(), request.getRequestURI());

        return ResponseEntity.ok("User logged out successfully");
    }
}
