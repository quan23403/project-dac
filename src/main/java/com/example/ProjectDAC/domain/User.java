package com.example.ProjectDAC.domain;

import com.example.ProjectDAC.util.constant.EStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Instant;

@Entity
@Table(name="user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message =  "Email không được để trống")
    private String email;
    @NotBlank(message =  "Password không được để trống")
    private String password;
    private String firstName;
    private String lastName;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    @Enumerated(EnumType.STRING)
    private EStatus status;

    @PrePersist
    public void handleBeforeCreate() {
        Instant instant = Instant.now();
        this.createdAt = Timestamp.from(instant);
        System.out.println(this.createdAt);
        this.status = EStatus.ACTIVE;
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        Instant instant = Instant.now();
        this.updatedAt = Timestamp.from(instant);
        System.out.println(this.updatedAt);
    }
}
