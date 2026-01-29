package com.AuthService.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtils { // Renamed for convention (Service, not Utils)

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    // IMPROVEMENT: Expiration time is now configurable from application.properties
    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ✅ Generate JWT token
    public String generateToken(UserDetails userDetails) {
        // IMPROVEMENT: Add user roles/authorities to the token claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                // MODERNIZED: The old signWith(Key, Algorithm) is deprecated.
                // The new method infers the algorithm from the SecretKey type.
                .signWith(getSigningKey())
                .compact();
    }

    // ✅ Validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    // ✅ Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ Check if token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ✅ Extract any claim using a function
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ✅ Extract all claims (This part was already using the modern builder)
    private Claims extractAllClaims(String token) {
        return Jwts.parser() // parserBuilder() is also fine, parser() is a shortcut
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token) // Use parseSignedClaims for JWS
                .getPayload();
    }
}