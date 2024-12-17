package com.example.ProjectDAC.domain;

import com.example.ProjectDAC.domain.dto.ForgotPassword;
import com.example.ProjectDAC.util.constant.EProvider;
import com.example.ProjectDAC.util.constant.EStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Instant;
import java.util.List;
import java.util.Set;

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
    private String password;
    private String firstName;
    private String lastName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Set<String> roles;

    @Enumerated(EnumType.STRING)
    private EProvider provider;

    @Enumerated(EnumType.STRING)
    private EStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_anken", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "anken_id"))
    List<Anken> ankenList;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    List<FileUpload> fileUploads;

    @OneToOne(mappedBy = "user")
    private ForgotPassword forgotPassword;

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
