package com.jobtrack.dto.application;

import com.jobtrack.entity.ApplicationPriority;
import com.jobtrack.entity.ApplicationSource;
import com.jobtrack.entity.ApplicationStatus;
import com.jobtrack.entity.EmploymentType;
import com.jobtrack.entity.WorkMode;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateJobApplicationRequest {

    private String companyName;

    private String jobTitle;

    private String jobUrl;

    private String location;

    private WorkMode workMode;

    private EmploymentType employmentType;

    @PositiveOrZero(message = "Minimum salary cannot be negative")
    private BigDecimal salaryMin;

    @PositiveOrZero(message = "Maximum salary cannot be negative")
    private BigDecimal salaryMax;

    private ApplicationSource source;

    private ApplicationStatus status;

    private ApplicationPriority priority;

    private LocalDate appliedDate;

    private LocalDate nextFollowUpDate;
}