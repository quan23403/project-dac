package com.example.ProjectDAC.domain.dto;

import com.example.ProjectDAC.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForgotPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long fpid;

    @Column(nullable = false)
    private int otp;

    @Column(nullable = false)
    private Date expirationTime;

    @Column(name = "active")
    private boolean active;

    @OneToOne
    private User user;
}
