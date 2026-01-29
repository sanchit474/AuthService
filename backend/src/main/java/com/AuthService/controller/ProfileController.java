package com.AuthService.controller;

import com.AuthService.io.ProfileRequest;
import com.AuthService.io.ProfileResponse;
import com.AuthService.service.EmailService;
import com.AuthService.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final EmailService emailService;
    @PostMapping("/register")
    public ProfileResponse register(@Valid @RequestBody ProfileRequest request) {
        ProfileResponse response = profileService.createProfile(request);
        // The email will now be sent after a user registers//        emailService.sendWelcomeEmail(response.getEmail(), response.getName());// Note: We removed emailService.sendWelcomeEmail() from here and add to verifyEmail() in profileService
        profileService.sendOtp(response.getEmail());
        return response;
    }
    // Inside ProfileController.java
    @PostMapping("/verify-otp")
    public void verifyEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email"); // ✅ Get email from Body, not Security Context
        String otp = request.get("otp");

        if (email == null || email.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (otp == null || otp.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP is required");
        }

        try {
            profileService.verifyOtp(email, otp);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(@CurrentSecurityContext(expression = "authentication?.name") String email){
       return profileService.getProfile(email);
    }
}

