package com.jobtrack.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobtrack.dto.application.CreateJobApplicationRequest;
import com.jobtrack.dto.application.JobApplicationResponse;
import com.jobtrack.dto.application.UpdateApplicationStatusRequest;
import com.jobtrack.dto.application.UpdateJobApplicationRequest;
import com.jobtrack.entity.ApplicationPriority;
import com.jobtrack.entity.ApplicationSource;
import com.jobtrack.entity.ApplicationStatus;
import com.jobtrack.entity.EmploymentType;
import com.jobtrack.entity.FollowUpType;
import com.jobtrack.entity.WorkMode;
import com.jobtrack.service.JobApplicationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {

	private final JobApplicationService jobApplicationService;

	public JobApplicationController(JobApplicationService jobApplicationService) {
		this.jobApplicationService = jobApplicationService;
	}

	@PostMapping
	public ResponseEntity<JobApplicationResponse> createApplication(
			@Valid @RequestBody CreateJobApplicationRequest request) {

		JobApplicationResponse response = jobApplicationService.createApplication(request);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<Page<JobApplicationResponse>> getMyApplications(

	        @PageableDefault(
	                size = 10,
	                sort = "createdAt",
	                direction = Sort.Direction.DESC
	        )
	        Pageable pageable,

	        @RequestParam(required = false)
	        ApplicationStatus status,

	        @RequestParam(required = false)
	        ApplicationPriority priority,

	        @RequestParam(required = false)
	        WorkMode workMode,

	        @RequestParam(required = false)
	        EmploymentType employmentType,

	        @RequestParam(required = false)
	        ApplicationSource source,

	        @RequestParam(required = false)
	        String search) {

	    Page<JobApplicationResponse> applications =
	            jobApplicationService.getMyApplications(
	                    pageable,
	                    status,
	                    priority,
	                    workMode,
	                    employmentType,
	                    source,
	                    search
	            );

	    return ResponseEntity.ok(applications);
	}

	@GetMapping("/{id}")
	public ResponseEntity<JobApplicationResponse> getApplicationById(@PathVariable Long id, Pageable pageable) {

		JobApplicationResponse response = jobApplicationService.getApplicationById(id);

		return ResponseEntity.ok(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<JobApplicationResponse> updateApplication(@PathVariable Long id,
			@Valid @RequestBody UpdateJobApplicationRequest request) {

		JobApplicationResponse response = jobApplicationService.updateApplication(id, request);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {

		jobApplicationService.deleteApplication(id);

		return ResponseEntity.noContent().build();
	}
	
	@PatchMapping("/{id}/status")
	public ResponseEntity<JobApplicationResponse> updateApplicationStatus(
	        @PathVariable Long id,
	        @Valid @RequestBody UpdateApplicationStatusRequest request) {

	    JobApplicationResponse response =
	            jobApplicationService.updateApplicationStatus(
	                    id,
	                    request
	            );

	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/follow-ups")
	public ResponseEntity<List<JobApplicationResponse>> getFollowUps(
	        @RequestParam FollowUpType type) {

	    List<JobApplicationResponse> applications =
	            jobApplicationService.getFollowUps(type);

	    return ResponseEntity.ok(applications);
	}
}