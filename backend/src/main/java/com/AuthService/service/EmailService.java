package com.AuthService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")              // app.prop
    private String fromMail;
    @Async // Add this annotation
    public void sendWelcomeEmail(String toEmail, String name){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromMail);
        message.setTo(toEmail);
        message.setSubject("Welcome to our platform");
        message.setText("Hello"+name+",\n\n Thanks for registering with us!\n\n Regards, \nQcare Team");
        mailSender.send(message);
    }
    public void sendResetOtpMail(String toEmail, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromMail);
        message.setTo(toEmail);
        message.setSubject("Password Reset OTP" );
        message.setText("Your otp for resetting your password is " + otp+" . use this otp to proceed with reset with password");
        mailSender.send(message);
    }
    public void sendOtpMail(String toEmail, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromMail);
        message.setTo(toEmail);
        message.setSubject("Account verification otp" );
        message.setText("Your otp  is " + otp+" . verify your account");
        mailSender.send(message);
    }
}
