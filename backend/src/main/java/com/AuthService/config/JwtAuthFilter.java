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

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

        // 1. Attempt to get JWT from Authorization header
        final String authHeader = request.getHeader("Authorization");
        String jwt = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        } else {
            // 1b. Fallback: try to extract JWT from cookie named "jwt"
            if (request.getCookies() != null) {
                for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                    if ("jwt".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }
        }

        // If we still don't have a token, continue the filter chain
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract the username (subject) from the token
        final String username = jwtUtils.extractUsername(jwt);

        // 3. Validate the token and set authentication if valid
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtils.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 4. Continue filter chain
        filterChain.doFilter(request, response);
    }
}
