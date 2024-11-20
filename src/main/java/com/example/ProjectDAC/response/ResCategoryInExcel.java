package com.example.ProjectDAC.response;

import com.example.ProjectDAC.util.constant.EKpiType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ResCategoryInExcel {
    private String nameAnken;
    private long categoryId;
    private String categoryName;
    private double budget;
    private EKpiType typeOfKPI;
    private long kpiGoal;
    private LocalDate startDate;
    private LocalDate endDate;

    public ResCategoryInExcel(String nameAnken, long categoryId, String categoryName, double budget, EKpiType typeOfKPI, long kpiGoal, LocalDate startDate, LocalDate endDate) {
        this.nameAnken = nameAnken;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.budget = budget;
        this.typeOfKPI = typeOfKPI;
        this.kpiGoal = kpiGoal;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ResCategoryInExcel() {

    }
}
