package com.example.ProjectDAC.request;

import com.example.ProjectDAC.util.constant.EKpiType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class UpdateCategoryRequest {
    private long id;
    private String name;
    private long budget;
    private Timestamp startDate;
    private Timestamp endDate;
    private EKpiType kpiType;
    private long kpiGoal;
}
