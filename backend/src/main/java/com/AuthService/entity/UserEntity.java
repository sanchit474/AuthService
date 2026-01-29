package com.AuthService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
//    id, userId, name, email, password, role, timeStamp, otpVerification,resetOtpVerification
//    unique:  id, userId, email
//    nullable: userId, email, password
//    Enum: role
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userId;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        PATIENT, DOCTOR, ADMIN
    }

    private String verifyOtp;

    private boolean accountVerified; // ✅ renamed for getter consistency

    private Long verifyOtpExpireAt;
    private String resetOtp;
    private Long resetOtpExpireAt;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
