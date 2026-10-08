package com.jobtrack.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jobtrack.dto.dashboard.DashboardSummaryResponse;
import com.jobtrack.entity.ApplicationStatus;
import com.jobtrack.entity.User;
import com.jobtrack.repository.InterviewRepository;
import com.jobtrack.repository.JobApplicationRepository;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private InterviewRepository interviewRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
    }

    @Test
    void shouldReturnDashboardSummarySuccessfully() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        // Application counts
        when(jobApplicationRepository.countByUserId(1L))
                .thenReturn(10L);

        when(jobApplicationRepository.countByUserIdAndStatus(
                1L, ApplicationStatus.SAVED))
                .thenReturn(2L);

        when(jobApplicationRepository.countByUserIdAndStatus(
                1L, ApplicationStatus.APPLIED))
                .thenReturn(3L);

        when(jobApplicationRepository.countByUserIdAndStatus(
                1L, ApplicationStatus.SCREENING))
                .thenReturn(1L);

        when(jobApplicationRepository.countByUserIdAndStatus(
                1L, ApplicationStatus.INTERVIEW))
                .thenReturn(2L);

        when(jobApplicationRepository.countByUserIdAndStatus(
                1L, ApplicationStatus.OFFER))
                .thenReturn(1L);

        when(jobApplicationRepository.countByUserIdAndStatus(
                1L, ApplicationStatus.REJECTED))
                .thenReturn(1L);

        when(jobApplicationRepository.countByUserIdAndStatus(
                1L, ApplicationStatus.WITHDRAWN))
                .thenReturn(0L);

        // Follow-ups
        when(jobApplicationRepository
                .countByUserIdAndNextFollowUpDateBefore(
                        eq(1L), any()))
                .thenReturn(2L);

        when(jobApplicationRepository
                .countByUserIdAndNextFollowUpDate(
                        eq(1L), any()))
                .thenReturn(1L);

        when(jobApplicationRepository
                .countByUserIdAndNextFollowUpDateAfter(
                        eq(1L), any()))
                .thenReturn(4L);

        // Upcoming interviews
        when(interviewRepository
                .findByApplicationUserIdAndInterviewDateAfterOrderByInterviewDateAsc(
                        eq(1L), any()))
                .thenReturn(List.of());

        // Analytics
        when(jobApplicationRepository
                .countApplicationsBySource(1L))
                .thenReturn(List.of());

        when(jobApplicationRepository
                .countApplicationsByWorkMode(1L))
                .thenReturn(List.of());

        when(jobApplicationRepository
                .countApplicationsByEmploymentType(1L))
                .thenReturn(List.of());

        // Execute
        DashboardSummaryResponse response =
                dashboardService.getSummary();

        // Verify
        assertNotNull(response);

        assertEquals(10L, response.getTotalApplications());
        assertEquals(2L, response.getSaved());
        assertEquals(3L, response.getApplied());
        assertEquals(1L, response.getScreening());
        assertEquals(2L, response.getInterview());
        assertEquals(1L, response.getOffers());
        assertEquals(1L, response.getRejected());
        assertEquals(0L, response.getWithdrawn());

        assertEquals(2L, response.getOverdueFollowUps());
        assertEquals(1L, response.getTodayFollowUps());
        assertEquals(4L, response.getUpcomingFollowUps());

        assertTrue(response.getUpcomingInterviews().isEmpty());
        assertTrue(response.getApplicationsBySource().isEmpty());
        assertTrue(response.getApplicationsByWorkMode().isEmpty());
        assertTrue(response.getApplicationsByEmploymentType().isEmpty());

        verify(currentUserService)
                .getCurrentUser();

        verify(jobApplicationRepository)
                .countByUserId(1L);
    }
}