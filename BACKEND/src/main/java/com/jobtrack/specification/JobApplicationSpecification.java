package com.jobtrack.specification;

import org.springframework.data.jpa.domain.Specification;

import com.jobtrack.entity.ApplicationPriority;
import com.jobtrack.entity.ApplicationSource;
import com.jobtrack.entity.ApplicationStatus;
import com.jobtrack.entity.EmploymentType;
import com.jobtrack.entity.JobApplication;
import com.jobtrack.entity.WorkMode;

public class JobApplicationSpecification {

    public static Specification<JobApplication> hasUserId(Long userId) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("user").get("id"),
                        userId
                );
    }

    public static Specification<JobApplication> hasStatus(
            ApplicationStatus status) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("status"),
                        status
                );
    }

    public static Specification<JobApplication> hasPriority(
            ApplicationPriority priority) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("priority"),
                        priority
                );
    }

    public static Specification<JobApplication> hasWorkMode(
            WorkMode workMode) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("workMode"),
                        workMode
                );
    }

    public static Specification<JobApplication> hasEmploymentType(
            EmploymentType employmentType) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("employmentType"),
                        employmentType
                );
    }

    public static Specification<JobApplication> hasSource(
            ApplicationSource source) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("source"),
                        source
                );
    }

    public static Specification<JobApplication> search(
            String search) {

        return (root, query, criteriaBuilder) -> {

            String pattern = "%" + search.toLowerCase() + "%";

            return criteriaBuilder.or(

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("companyName")),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("jobTitle")),
                            pattern
                    ),

                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("location")),
                            pattern
                    )
            );
        };
    }
}