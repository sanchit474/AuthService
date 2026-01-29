// From: AppUserDetailService.java

package com.AuthService.service;

import com.AuthService.entity.UserEntity;
import com.AuthService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Import this
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections; // Import this

@Service
@RequiredArgsConstructor
public class AppUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for email: " + email));
        // ADD THIS BLOCK:
        if (!existingUser.isAccountVerified()) {
            throw new UsernameNotFoundException("Account not verified. Please verify your email.");
            // Note: Ideally, use a custom exception like DisabledException,
            // but UsernameNotFoundException will also block the login safely.
        }
        // FIX: Create a GrantedAuthority from the user's role and pass it to the User object.
        // The role from your enum needs to be a string like "ROLE_PATIENT" for Spring Security's conventions.
        // We will assume a "ROLE_" prefix is not needed unless you configure it elsewhere.
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(existingUser.getRole().name());

        return new User(
                existingUser.getEmail(),
                existingUser.getPassword(),
                Collections.singletonList(authority)); // Pass the user's actual role
    }
}
