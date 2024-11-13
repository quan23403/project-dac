package com.example.ProjectDAC.domain;

import com.example.ProjectDAC.util.constant.EKpiType;
import com.example.ProjectDAC.util.constant.EStatus;
import com.example.ProjectDAC.util.constant.ETypeCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name="category")
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Name Category khong duoc de trong")
    private String name;

    @NotNull(message = "Type khong duoc de trong")
    @Enumerated(EnumType.STRING)
    private ETypeCategory typeCategory;
    private double budget;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private EKpiType kpiType;
    private long kpiGoal;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    @Enumerated(EnumType.STRING)
    private EStatus status;

    @PrePersist
    public void handleBeforeCreate() {
        Instant instant = Instant.now();
        this.createdAt = Timestamp.from(instant);
        this.status = EStatus.ACTIVE;
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        Instant instant = Instant.now();
        this.updatedAt = Timestamp.from(instant);
        System.out.println(this.updatedAt);
    }
}
