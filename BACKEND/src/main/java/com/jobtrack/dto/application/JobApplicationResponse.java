package com.jobtrack.dto.application;

import com.jobtrack.entity.ApplicationPriority;
import com.jobtrack.entity.ApplicationSource;
import com.jobtrack.entity.ApplicationStatus;
import com.jobtrack.entity.EmploymentType;
import com.jobtrack.entity.WorkMode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class JobApplicationResponse {

    private Long id;
    private String companyName;
    private String jobTitle;
    private String jobUrl;
    private String location;
    private WorkMode workMode;
    private EmploymentType employmentType;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private ApplicationSource source;
    private ApplicationStatus status;
    private ApplicationPriority priority;
    private LocalDate appliedDate;
    private LocalDate nextFollowUpDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}