//package com.QCare.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.lang.NonNull;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Arrays;
//
//@Component
//public class JwtAuthFilter extends OncePerRequestFilter {
//
//    private final JwtUtils jwtUtils;
//    private final UserDetailsService userDetailsService;
//
//    public JwtAuthFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
//        this.jwtUtils = jwtUtils;
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Override
//    protected void doFilterInternal(
//            @NonNull HttpServletRequest request,
//            @NonNull HttpServletResponse response,
//            @NonNull FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        String jwt = extractJwtFromCookie(request);
//
//        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//            String username = jwtUtils.extractUsername(jwt);
//
//            if (username != null) {
//                UserDetails userDetails =
//                        userDetailsService.loadUserByUsername(username);
//
//                if (jwtUtils.validateToken(jwt, userDetails)) {
//
//                    UsernamePasswordAuthenticationToken authToken =
//                            new UsernamePasswordAuthenticationToken(
//                                    userDetails,
//                                    null,
//                                    userDetails.getAuthorities()
//                            );
//
//                    authToken.setDetails(
//                            new WebAuthenticationDetailsSource()
//                                    .buildDetails(request)
//                    );
//
//                    SecurityContextHolder.getContext()
//                            .setAuthentication(authToken);
//                }
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private String extractJwtFromCookie(HttpServletRequest request) {
//        if (request.getCookies() == null) return null;
//
//        return Arrays.stream(request.getCookies())
//                .filter(cookie -> "jwt".equals(cookie.getName()))
//                .map(Cookie::getValue)
//                .findFirst()
//                .orElse(null);
//    }
//}

package com.AuthService.config;

//package com.qcare.security; // Or your security package

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Get the Authorization header from the request
        final String authHeader = request.getHeader("Authorization");

        // 2. Check if the header is null or doesn't start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // If so, pass the request to the next filter and exit
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the JWT from the header (remove "Bearer ")
        final String jwt = authHeader.substring(7);

        // 4. Extract the username (subject) from the token
        final String username = jwtUtils.extractUsername(jwt);

        // 5. Validate the token
        // Check if username is not null AND the user is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Check if the token is valid for the loaded user
            if (jwtUtils.validateToken(jwt, userDetails)) {
                // 6. If the token is valid, create an authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Credentials are not needed for token-based auth
                        userDetails.getAuthorities()
                );

                // Add more details from the request to the auth token
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 7. Update the SecurityContextHolder with the new authentication token
                // This step marks the user as authenticated for this request
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 8. Pass the request to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
