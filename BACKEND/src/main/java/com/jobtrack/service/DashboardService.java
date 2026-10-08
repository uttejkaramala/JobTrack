package com.jobtrack.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jobtrack.dto.dashboard.ApplicationsByEmploymentTypeResponse;
import com.jobtrack.dto.dashboard.ApplicationsBySourceResponse;
import com.jobtrack.dto.dashboard.ApplicationsByWorkModeResponse;
import com.jobtrack.dto.dashboard.DashboardSummaryResponse;
import com.jobtrack.dto.dashboard.UpcomingInterviewResponse;
import com.jobtrack.entity.ApplicationStatus;
import com.jobtrack.entity.Interview;
import com.jobtrack.entity.User;
import com.jobtrack.repository.InterviewRepository;
import com.jobtrack.repository.JobApplicationRepository;

@Service
public class DashboardService {

    private final JobApplicationRepository jobApplicationRepository;
    private final CurrentUserService currentUserService;
    private final InterviewRepository interviewRepository;

    public DashboardService(
            JobApplicationRepository jobApplicationRepository,
            CurrentUserService currentUserService,
            InterviewRepository interviewRepository) {

        this.jobApplicationRepository = jobApplicationRepository;
        this.currentUserService = currentUserService;
        this.interviewRepository = interviewRepository;
    }

    public DashboardSummaryResponse getSummary() {

        User currentUser = currentUserService.getCurrentUser();

        Long userId = currentUser.getId();

        long totalApplications =
                jobApplicationRepository.countByUserId(userId);

        long saved =
                jobApplicationRepository.countByUserIdAndStatus(
                        userId,
                        ApplicationStatus.SAVED);

        long applied =
                jobApplicationRepository.countByUserIdAndStatus(
                        userId,
                        ApplicationStatus.APPLIED);

        long screening =
                jobApplicationRepository.countByUserIdAndStatus(
                        userId,
                        ApplicationStatus.SCREENING);

        long interview =
                jobApplicationRepository.countByUserIdAndStatus(
                        userId,
                        ApplicationStatus.INTERVIEW);

        long offers =
                jobApplicationRepository.countByUserIdAndStatus(
                        userId,
                        ApplicationStatus.OFFER);

        long rejected =
                jobApplicationRepository.countByUserIdAndStatus(
                        userId,
                        ApplicationStatus.REJECTED);

        long withdrawn =
                jobApplicationRepository.countByUserIdAndStatus(
                        userId,
                        ApplicationStatus.WITHDRAWN);
        
        
        
        LocalDate today = LocalDate.now();

        long overdueFollowUps =
                jobApplicationRepository
                        .countByUserIdAndNextFollowUpDateBefore(
                                userId,
                                today);

        long todayFollowUps =
                jobApplicationRepository
                        .countByUserIdAndNextFollowUpDate(
                                userId,
                                today);

        long upcomingFollowUps =
                jobApplicationRepository
                        .countByUserIdAndNextFollowUpDateAfter(
                                userId,
                                today);
        
        
        List<Interview> upcomingInterviews =
                interviewRepository
                        .findByApplicationUserIdAndInterviewDateAfterOrderByInterviewDateAsc(
                                userId,
                                LocalDateTime.now());
        
        List<UpcomingInterviewResponse> upcomingInterviewResponses =
                upcomingInterviews.stream()
                        .limit(5)
                        .map(interviews -> new UpcomingInterviewResponse(
                                interviews.getId(),
                                interviews.getApplication().getId(),
                                interviews.getApplication().getCompanyName(),
                                interviews.getApplication().getJobTitle(),
                                interviews.getRoundName(),
                                interviews.getInterviewDate(),
                                interviews.getInterviewType().name(),
                                interviews.getStatus().name()
                        ))
                        .toList();
        
        
        List<Object[]> sourceResults =
                jobApplicationRepository.countApplicationsBySource(userId);

        List<ApplicationsBySourceResponse> applicationsBySource =
                sourceResults.stream()
                        .map(row -> new ApplicationsBySourceResponse(
                                row[0].toString(),
                                ((Number) row[1]).longValue()
                        ))
                        .toList();
        

        List<Object[]> workModeResults =
                jobApplicationRepository
                        .countApplicationsByWorkMode(userId);

        List<ApplicationsByWorkModeResponse> applicationsByWorkMode =
                workModeResults.stream()
                        .map(row -> new ApplicationsByWorkModeResponse(
                                row[0] != null ? row[0].toString() : "UNKNOWN",
                                ((Number) row[1]).longValue()
                        ))
                        .toList();
        
        List<Object[]> employmentTypeResults =
                jobApplicationRepository
                        .countApplicationsByEmploymentType(userId);

        List<ApplicationsByEmploymentTypeResponse> applicationsByEmploymentType =
                employmentTypeResults.stream()
                        .map(row -> new ApplicationsByEmploymentTypeResponse(
                                row[0] != null ? row[0].toString() : "UNKNOWN",
                                ((Number) row[1]).longValue()
                        ))
                        .toList();
        
        
        
        return new DashboardSummaryResponse(
                totalApplications,
                saved,
                applied,
                screening,
                interview,
                offers,
                rejected,
                withdrawn,
                
                overdueFollowUps,
                todayFollowUps,
                upcomingFollowUps,
                
                upcomingInterviewResponses,
                
                applicationsBySource,
                
                applicationsByWorkMode,
                
                applicationsByEmploymentType
        );
    }
}