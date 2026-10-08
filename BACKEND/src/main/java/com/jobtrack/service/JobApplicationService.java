package com.jobtrack.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.jobtrack.dto.application.CreateJobApplicationRequest;
import com.jobtrack.dto.application.JobApplicationResponse;
import com.jobtrack.dto.application.UpdateApplicationStatusRequest;
import com.jobtrack.dto.application.UpdateJobApplicationRequest;
import com.jobtrack.entity.ApplicationPriority;
import com.jobtrack.entity.ApplicationSource;
import com.jobtrack.entity.ApplicationStatus;
import com.jobtrack.entity.EmploymentType;
import com.jobtrack.entity.FollowUpType;
import com.jobtrack.entity.JobApplication;
import com.jobtrack.entity.User;
import com.jobtrack.entity.WorkMode;
import com.jobtrack.exception.ResourceNotFoundException;
import com.jobtrack.repository.JobApplicationRepository;
import com.jobtrack.specification.JobApplicationSpecification;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final CurrentUserService currentUserService;
    private final JobApplicationValidationService validationService;

    public JobApplicationService(
            JobApplicationRepository jobApplicationRepository, 
            CurrentUserService currentUserService,
            JobApplicationValidationService validationService) {
    	
        this.jobApplicationRepository = jobApplicationRepository;
        this.currentUserService = currentUserService;
        this.validationService = validationService;
    }

    public JobApplicationResponse createApplication(
            CreateJobApplicationRequest request) {

    	//calling validation service to check validate salary, and dates
    	validationService.validateCreate(request);
    	
        User currentUser = currentUserService.getCurrentUser();

        JobApplication application = new JobApplication();

        application.setCompanyName(request.getCompanyName());
        application.setJobTitle(request.getJobTitle());
        application.setJobUrl(request.getJobUrl());
        application.setLocation(request.getLocation());
        application.setWorkMode(request.getWorkMode());
        application.setEmploymentType(request.getEmploymentType());
        application.setSalaryMin(request.getSalaryMin());
        application.setSalaryMax(request.getSalaryMax());
        application.setSource(request.getSource());
        application.setPriority(request.getPriority());
        application.setAppliedDate(request.getAppliedDate());
        application.setNextFollowUpDate(request.getNextFollowUpDate());

        // Associate application with logged-in user
        application.setUser(currentUser);

        JobApplication savedApplication =
                jobApplicationRepository.save(application);

        return mapToResponse(savedApplication);
    }
    
    public Page<JobApplicationResponse> getMyApplications(
            Pageable pageable,
            ApplicationStatus status,
            ApplicationPriority priority,
            WorkMode workMode,
            EmploymentType employmentType,
            ApplicationSource source,
            String search) {

        User currentUser = currentUserService.getCurrentUser();

        Specification<JobApplication> specification =
                JobApplicationSpecification.hasUserId(
                        currentUser.getId());

        if (status != null) {
            specification = specification.and(
                    JobApplicationSpecification.hasStatus(status));
        }

        if (priority != null) {
            specification = specification.and(
                    JobApplicationSpecification.hasPriority(priority));
        }

        if (workMode != null) {
            specification = specification.and(
                    JobApplicationSpecification.hasWorkMode(workMode));
        }

        if (employmentType != null) {
            specification = specification.and(
                    JobApplicationSpecification.hasEmploymentType(
                            employmentType));
        }

        if (source != null) {
            specification = specification.and(
                    JobApplicationSpecification.hasSource(source));
        }

        if (search != null && !search.isBlank()) {
            specification = specification.and(
                    JobApplicationSpecification.search(search));
        }

        Page<JobApplication> applications =
                jobApplicationRepository.findAll(
                        specification,
                        pageable);

        return applications.map(this::mapToResponse);
    }
   
    
    
    public JobApplicationResponse getApplicationById(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        JobApplication application =
                jobApplicationRepository
                        .findByIdAndUserId(id, currentUser.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job application not found with id: " + id));

        return mapToResponse(application);
    }
    
    
    public JobApplicationResponse updateApplication(
            Long id,
            UpdateJobApplicationRequest request) {
    	
    	//calling validation to check salary and date
    	validationService.validateUpdate(request);

        User currentUser = currentUserService.getCurrentUser();

        JobApplication application =
                jobApplicationRepository
                        .findByIdAndUserId(id, currentUser.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job application not found with id: " + id));

        application.setCompanyName(request.getCompanyName());
        application.setJobTitle(request.getJobTitle());
        application.setJobUrl(request.getJobUrl());
        application.setLocation(request.getLocation());
        application.setWorkMode(request.getWorkMode());
        application.setEmploymentType(request.getEmploymentType());
        application.setSalaryMin(request.getSalaryMin());
        application.setSalaryMax(request.getSalaryMax());
        application.setSource(request.getSource());
        application.setStatus(request.getStatus());
        application.setPriority(request.getPriority());
        application.setAppliedDate(request.getAppliedDate());
        application.setNextFollowUpDate(request.getNextFollowUpDate());

        JobApplication updatedApplication =
                jobApplicationRepository.save(application);

        return mapToResponse(updatedApplication);
    }
    
    public void deleteApplication(Long id) {

        User currentUser = currentUserService.getCurrentUser();

        JobApplication application =
                jobApplicationRepository
                        .findByIdAndUserId(id, currentUser.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job application not found with id: " + id));

        jobApplicationRepository.delete(application);
    }
    
	public JobApplicationResponse updateApplicationStatus(Long id, UpdateApplicationStatusRequest request) {

		User currentUser = currentUserService.getCurrentUser();

		JobApplication application = jobApplicationRepository
				.findByIdAndUserId(id, currentUser.getId())
				.orElseThrow(() -> new ResourceNotFoundException("Job application not found with id: " + id));

		application.setStatus(request.getStatus());

		JobApplication updatedApplication = jobApplicationRepository.save(application);

		return mapToResponse(updatedApplication);
	}
    
	public List<JobApplicationResponse> getFollowUps(
	        FollowUpType type) {

	    User currentUser =
	            currentUserService.getCurrentUser();

	    LocalDate today = LocalDate.now();

	    List<JobApplication> applications;

	    switch (type) {

	        case OVERDUE:
	            applications =
	                    jobApplicationRepository
	                            .findByUserIdAndNextFollowUpDateLessThan(
	                                    currentUser.getId(),
	                                    today);
	            break;

	        case TODAY:
	            applications =
	                    jobApplicationRepository
	                            .findByUserIdAndNextFollowUpDate(
	                                    currentUser.getId(),
	                                    today);
	            break;

	        case UPCOMING:
	            applications =
	                    jobApplicationRepository
	                            .findByUserIdAndNextFollowUpDateGreaterThan(
	                                    currentUser.getId(),
	                                    today);
	            break;

	        default:
	            throw new IllegalArgumentException(
	                    "Invalid follow-up type");
	    }

	    return applications.stream()
	            .map(this::mapToResponse)
	            .toList();
	}
	
    private JobApplicationResponse mapToResponse(
            JobApplication application) {

        return new JobApplicationResponse(
                application.getId(),
                application.getCompanyName(),
                application.getJobTitle(),
                application.getJobUrl(),
                application.getLocation(),
                application.getWorkMode(),
                application.getEmploymentType(),
                application.getSalaryMin(),
                application.getSalaryMax(),
                application.getSource(),
                application.getStatus(),
                application.getPriority(),
                application.getAppliedDate(),
                application.getNextFollowUpDate(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }
}