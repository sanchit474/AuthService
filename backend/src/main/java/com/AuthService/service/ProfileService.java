package com.AuthService.service;

import com.AuthService.io.ProfileRequest;
import com.AuthService.io.ProfileResponse;

public interface ProfileService {
   //for registration
   ProfileResponse createProfile(ProfileRequest request);
   //for login
   ProfileResponse getProfile(String email);

   //for registration verification
   void sendOtp(String email);
   void verifyOtp(String email, String otp);

   //password reset
   void sendResetOtp(String email);
   void resetPassword(String email, String otp, String newPassword);
   String getLoggedInUserId(String email);
}
