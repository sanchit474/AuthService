package com.AuthService.controller;

import com.AuthService.config.JwtUtils;
import com.AuthService.io.AuthRequest;
import com.AuthService.io.AuthResponse;
import com.AuthService.io.ForgotPasswordRequest;
import com.AuthService.io.ResetPasswordRequest;
import com.AuthService.service.AppUserDetailService;
import com.AuthService.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AppUserDetailService appUserDetailService;
    private final JwtUtils jwtUtil;
    private final ProfileService profileService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            // Authenticate user
            authenticate(request.getEmail(), request.getPassword());
            // Load user details
            final UserDetails userDetails = appUserDetailService.loadUserByUsername(request.getEmail());
            // Generate JWT token
            final String jwtToken = jwtUtil.generateToken(userDetails);
            // Create HttpOnly cookie with JWT
            ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .sameSite("Strict")
                    .build();
            // Build response body (optional)
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("email", request.getEmail());
            responseBody.put("token", jwtToken);
            // Return response with cookie
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new AuthResponse(request.getEmail(), jwtToken));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }

    private void authenticate(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
    }

    @PostMapping("/send-reset-otp")
    public ResponseEntity<String> sendResetOtp(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            profileService.sendResetOtp(request.getEmail());
            // Production Tip: Return a standard message.
            // Even if email doesn't exist, say "Sent" to prevent hackers from guessing valid emails.
            return ResponseEntity.ok("If an account exists for this email, an OTP has been sent.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error sending OTP");
        }
    }

    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            profileService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }
}

//    @PostMapping("/send-reset-otp")
//    public void sendResetOtp(@RequestParam String email) {
//        try {
//            profileService.sendResetOtp(email);
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
//        }
//    }

//    @GetMapping("/is-authenticated")
//    public ResponseEntity<Boolean> isAuthenticated(@CurrentSecurityContext(expression = "authenticated?.name") String email){
//        return ResponseEntity.ok(email != null);
//    }
//    @PostMapping("/send-otp")
//    public void sendVerifyOtp(@CurrentSecurityContext(expression = "authentication?.name") String email){
//        try{
//            profileService.sendOtp(email);
//        }catch (Exception e){
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
//        }
//    }
// Inside AuthController.java in the verifyEmail method
//    @PostMapping("/verify-otp")
//    public void verifyEmail(@RequestBody Map<String, Object> request, @CurrentSecurityContext(expression = "authentication?.name") String email){
//        // CORRECTED CODE:
//        if(request.get("otp") ==  null || request.get("otp").toString().isEmpty()){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"OTP is missing");
//        }
//        try{
//            profileService.verifyOtp(email, request.get("otp").toString());
//        } catch (Exception e){
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
//        }
//    }