package com.jobtrack.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jobtrack.dto.interview.CreateInterviewRequest;
import com.jobtrack.dto.interview.InterviewResponse;
import com.jobtrack.dto.interview.UpdateInterviewRequest;
import com.jobtrack.entity.Interview;
import com.jobtrack.entity.InterviewStatus;
import com.jobtrack.entity.JobApplication;
import com.jobtrack.entity.User;
import com.jobtrack.exception.ResourceNotFoundException;
import com.jobtrack.repository.InterviewRepository;
import com.jobtrack.repository.JobApplicationRepository;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final CurrentUserService currentUserService;

    public InterviewService(
            InterviewRepository interviewRepository,
            JobApplicationRepository jobApplicationRepository,
            CurrentUserService currentUserService) {

        this.interviewRepository = interviewRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.currentUserService = currentUserService;
    }

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------

    public InterviewResponse createInterview(
            Long applicationId,
            CreateInterviewRequest request) {

        User currentUser =
                currentUserService.getCurrentUser();

        JobApplication application =
                jobApplicationRepository
                        .findByIdAndUserId(
                                applicationId,
                                currentUser.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Job application not found with id: "
                                                + applicationId));

        Interview interview = new Interview();

        interview.setRoundName(request.getRoundName());
        interview.setInterviewDate(request.getInterviewDate());
        interview.setInterviewType(request.getInterviewType());
        interview.setNotes(request.getNotes());

        // Default status is SCHEDULED
        interview.setStatus(InterviewStatus.SCHEDULED);

        // Associate interview with application
        interview.setApplication(application);

        Interview savedInterview =
                interviewRepository.save(interview);

        return mapToResponse(savedInterview);
    }


    // ---------------------------------------------------------
    // GET ALL INTERVIEWS FOR AN APPLICATION
    // ---------------------------------------------------------

    public List<InterviewResponse> getInterviewsForApplication(
            Long applicationId) {

        User currentUser =
                currentUserService.getCurrentUser();

        // First verify that application belongs to current user
        jobApplicationRepository
                .findByIdAndUserId(
                        applicationId,
                        currentUser.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job application not found with id: "
                                        + applicationId));

        List<Interview> interviews =
                interviewRepository.findByApplicationId(
                        applicationId);

        return interviews.stream()
                .map(this::mapToResponse)
                .toList();
    }


    // ---------------------------------------------------------
    // GET ONE INTERVIEW
    // ---------------------------------------------------------

    public InterviewResponse getInterviewById(Long id) {

        User currentUser =
                currentUserService.getCurrentUser();

        Interview interview =
                interviewRepository
                        .findByIdAndApplicationUserId(
                                id,
                                currentUser.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview not found with id: "
                                                + id));

        return mapToResponse(interview);
    }


    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------

    public InterviewResponse updateInterview(
            Long id,
            UpdateInterviewRequest request) {

        User currentUser =
                currentUserService.getCurrentUser();

        Interview interview =
                interviewRepository
                        .findByIdAndApplicationUserId(
                                id,
                                currentUser.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview not found with id: "
                                                + id));

        interview.setRoundName(request.getRoundName());
        interview.setInterviewDate(request.getInterviewDate());
        interview.setInterviewType(request.getInterviewType());
        interview.setStatus(request.getStatus());
        interview.setNotes(request.getNotes());

        Interview updatedInterview =
                interviewRepository.save(interview);

        return mapToResponse(updatedInterview);
    }


    // ---------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------

    public void deleteInterview(Long id) {

        User currentUser =
                currentUserService.getCurrentUser();

        Interview interview =
                interviewRepository
                        .findByIdAndApplicationUserId(
                                id,
                                currentUser.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview not found with id: "
                                                + id));

        interviewRepository.delete(interview);
    }


    // ---------------------------------------------------------
    // UPDATE STATUS
    // ---------------------------------------------------------

    public InterviewResponse updateInterviewStatus(
            Long id,
            InterviewStatus status) {

        User currentUser =
                currentUserService.getCurrentUser();

        Interview interview =
                interviewRepository
                        .findByIdAndApplicationUserId(
                                id,
                                currentUser.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview not found with id: "
                                                + id));

        interview.setStatus(status);

        Interview updatedInterview =
                interviewRepository.save(interview);

        return mapToResponse(updatedInterview);
    }


    // ---------------------------------------------------------
    // ENTITY → RESPONSE DTO
    // ---------------------------------------------------------

    private InterviewResponse mapToResponse(
            Interview interview) {

        return new InterviewResponse(
                interview.getId(),
                interview.getRoundName(),
                interview.getInterviewDate(),
                interview.getInterviewType(),
                interview.getStatus(),
                interview.getNotes(),
                interview.getCreatedAt(),
                interview.getUpdatedAt()
        );
    }
}