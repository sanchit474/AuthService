package com.AuthService.service;

import com.AuthService.entity.UserEntity;
import com.AuthService.io.ProfileRequest;
import com.AuthService.io.ProfileResponse;
import com.AuthService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // Inside ProfileServiceImpl.java
    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        // FIX: Explicitly check if email exists to ensure the correct error message
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        UserEntity newProfile = convertToUserEntity(request);

        // You can remove the try-catch block now since we checked the email above
        newProfile = userRepository.save(newProfile);

        return convertToProfileResponse(newProfile);
    }

    @Override
    public ProfileResponse getProfile(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("user not found" + email));
        return convertToProfileResponse(existingUser);
    }
    // From: ProfileServiceImpl.java

    private UserEntity convertToUserEntity(ProfileRequest request) {
        return UserEntity.builder()
                .userId(UUID.randomUUID().toString())
                .fullName(request.getName())
                .email(request.getEmail())
                // FIX: Removed the incorrect comment. The password is now correctly encoded.
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserEntity.Role.PATIENT)
                .accountVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .build();
    }

    private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
        return ProfileResponse.builder()
                .userId(newProfile.getUserId())
                .name(newProfile.getFullName())
                .email(newProfile.getEmail())
                .isAccountVerified(newProfile.isAccountVerified())
                .build();
    }

    @Override
    public void sendOtp(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("user not found :" + email));
        if(existingUser.isAccountVerified()){
            return;
        }
        //generate 6 digit otp
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        //calculate expiry time
        long expiryTime = System.currentTimeMillis() +(15*60*1000);

        //update the profile/user
        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpireAt(expiryTime);
        //save to db
        userRepository.save(existingUser);

        try{
            emailService.sendOtpMail(existingUser.getEmail(), otp);

        }catch (Exception e) {
            throw new RuntimeException("unable to send email");
        }


    }

    @Override
    public void verifyOtp(String email, String otp) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("user not found :" + email));
        if(existingUser.getVerifyOtp() == null || !existingUser.getVerifyOtp().equals(otp)){
            throw new RuntimeException("Invalid OTP");
        }
        if(existingUser.getVerifyOtpExpireAt() < System.currentTimeMillis()){
            throw new RuntimeException("OTP Expired");
        }
        existingUser.setAccountVerified(true);
        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpireAt(0L);

        userRepository.save(existingUser);

        // 3. ✅ NEW: Send Welcome Email here (after success)
        try {
            emailService.sendWelcomeEmail(existingUser.getEmail(), existingUser.getFullName());
        } catch (Exception e) {
            // Log this error, but don't fail the verification just because email failed
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }

    @Override
    public void sendResetOtp(String email) {
        UserEntity existingEntity = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("user not found :" + email));
        //generate 6 dig otp
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        //calculate expiry time
        long expiryTime = System.currentTimeMillis() +(15*60*1000);
        //update the profile/user
        existingEntity.setResetOtp(otp);
        existingEntity.setResetOtpExpireAt(expiryTime);

        // save into the database;
        userRepository.save(existingEntity);
        try{
//            TODO: send reset otp email
            emailService.sendResetOtpMail(existingEntity.getEmail(), otp);

        }catch (Exception e){
            throw new RuntimeException("Unable to send the otp");
        }
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("user not found :" + email));
        if(existingUser.getResetOtp() == null || !existingUser.getResetOtp().equals(otp)){
            throw new RuntimeException("Invalid otp");
        }
        if(existingUser.getResetOtpExpireAt() < System.currentTimeMillis()){
            throw new RuntimeException("OTP Expired");
        }

        existingUser.setPassword(passwordEncoder.encode(newPassword));
        existingUser.setResetOtp(null);
        existingUser.setResetOtpExpireAt(0L);

        userRepository.save(existingUser);
    }
    @Override
    public String getLoggedInUserId(String email) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("user not found :" + email));
        return existingUser.getUserId();
    }


}
