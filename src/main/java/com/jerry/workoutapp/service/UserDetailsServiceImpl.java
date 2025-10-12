package com.jerry.workoutapp.service;

import com.jerry.workoutapp.entity.User;
import com.jerry.workoutapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.jerry.workoutapp.util.SecurityConstants.ROLE_PREFIX;

/**
 * User Details Service Implementation
 * Loads user details from database for Spring Security authentication
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Load user details by email for authentication
     * Converts database User entity to Spring Security UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);

        // Check if user exists
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }

        // Convert database User to Spring Security UserDetails
        return user.map(value -> org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password(value.getPasswordHash()) // Encrypted password from database
                .authorities(ROLE_PREFIX + value.getRole()).build()).orElse(null); // Add "ROLE_" prefix to user's role
    }

}
