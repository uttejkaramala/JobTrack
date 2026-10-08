package com.jobtrack.service;

import org.springframework.stereotype.Service;

import com.jobtrack.dto.application.CreateJobApplicationRequest;
import com.jobtrack.dto.application.UpdateJobApplicationRequest;

@Service
public class JobApplicationValidationService {

    public void validateCreate(
            CreateJobApplicationRequest request) {

        validateSalary(
                request.getSalaryMin(),
                request.getSalaryMax()
        );

        validateFollowUpDate(
                request.getAppliedDate(),
                request.getNextFollowUpDate()
        );
    }

    public void validateUpdate(
            UpdateJobApplicationRequest request) {

        validateSalary(
                request.getSalaryMin(),
                request.getSalaryMax()
        );

        validateFollowUpDate(
                request.getAppliedDate(),
                request.getNextFollowUpDate()
        );
    }

    private void validateSalary(
            java.math.BigDecimal salaryMin,
            java.math.BigDecimal salaryMax) {

        if (salaryMin != null &&
            salaryMax != null &&
            salaryMin.compareTo(salaryMax) > 0) {

            throw new IllegalArgumentException(
                    "Minimum salary cannot be greater than maximum salary"
            );
        }
    }

    private void validateFollowUpDate(
            java.time.LocalDate appliedDate,
            java.time.LocalDate nextFollowUpDate) {

        if (appliedDate != null &&
            nextFollowUpDate != null &&
            nextFollowUpDate.isBefore(appliedDate)) {

            throw new IllegalArgumentException(
                    "Follow-up date cannot be before applied date"
            );
        }
    }
}