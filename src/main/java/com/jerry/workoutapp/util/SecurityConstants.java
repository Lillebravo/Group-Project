package com.jerry.workoutapp.util;

public final class SecurityConstants {

    // Role prefix used by Spring Security
    public static final String ROLE_PREFIX = "ROLE_";

    // Authorization header constants
    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    // Private constructor to prevent instantiation
    private SecurityConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}