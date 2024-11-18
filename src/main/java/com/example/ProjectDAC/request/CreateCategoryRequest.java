package com.example.ProjectDAC.request;

import com.example.ProjectDAC.util.constant.EKpiType;
import com.example.ProjectDAC.util.constant.ETypeCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateCategoryRequest {
    private long id;
    private ETypeCategory typeCategory;
    private String name;
    private double budget;
    private LocalDate startDate;
    private LocalDate endDate;
    private EKpiType kpiType;
    private long kpiGoal;
    private String nameAnken;
}
