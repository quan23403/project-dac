package com.example.ProjectDAC.domain.dto;

import com.example.ProjectDAC.domain.Anken;
import com.example.ProjectDAC.util.constant.EKpiType;
import com.example.ProjectDAC.util.constant.EStatus;
import com.example.ProjectDAC.util.constant.ETypeCategory;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ResCategoryDTO {
    private long id;
    private String name;

    @Enumerated(EnumType.STRING)
    private ETypeCategory typeCategory;
    private double budget;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private EStatus status;

    @Enumerated(EnumType.STRING)
    private EKpiType kpiType;
    private long kpiGoal;
    private long ankenId;
    private String ankenName;
}
