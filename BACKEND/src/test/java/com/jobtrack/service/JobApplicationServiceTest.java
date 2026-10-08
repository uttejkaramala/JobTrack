package com.jobtrack.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jobtrack.dto.application.CreateJobApplicationRequest;
import com.jobtrack.dto.application.JobApplicationResponse;
import com.jobtrack.entity.JobApplication;
import com.jobtrack.entity.User;
import com.jobtrack.exception.ResourceNotFoundException;
import com.jobtrack.repository.JobApplicationRepository;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private CurrentUserService currentUserService;
    
    @Mock
    private JobApplicationValidationService validationService;

    @InjectMocks
    private JobApplicationService jobApplicationService;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();

        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
    }
    
    @Test
    void shouldCreateApplicationSuccessfully() {

        CreateJobApplicationRequest request =
                new CreateJobApplicationRequest();

        request.setCompanyName("Google");
        request.setJobTitle("Java Developer");
        request.setSalaryMin(BigDecimal.valueOf(500000));
        request.setSalaryMax(BigDecimal.valueOf(800000));

        // Current logged-in user
        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        // Validation passes
        doNothing().when(validationService)
                .validateCreate(request);

        // Repository saves the application
        when(jobApplicationRepository.save(any(JobApplication.class)))
                .thenAnswer(invocation -> {
                    JobApplication application =
                            invocation.getArgument(0);

                    application.setId(1L);

                    return application;
                });

        // Execute
        JobApplicationResponse response =
                jobApplicationService.createApplication(request);

        // Verify response
        assertNotNull(response);
        assertEquals("Google", response.getCompanyName());
        assertEquals("Java Developer", response.getJobTitle());

        // Verify important interactions
        verify(validationService).validateCreate(request);
        verify(currentUserService).getCurrentUser();
        verify(jobApplicationRepository)
                .save(any(JobApplication.class));
    }
    
    @Test
    void shouldRejectInvalidSalaryRange() {

        CreateJobApplicationRequest request =
                new CreateJobApplicationRequest();

        request.setCompanyName("Google");
        request.setJobTitle("Java Developer");
        request.setSalaryMin(BigDecimal.valueOf(900000));
        request.setSalaryMax(BigDecimal.valueOf(500000));

        doThrow(new IllegalArgumentException(
                "Minimum salary cannot be greater than maximum salary"))
                .when(validationService)
                .validateCreate(request);

        assertThrows(
                IllegalArgumentException.class,
                () -> jobApplicationService.createApplication(request)
        );

        verify(validationService).validateCreate(request);

        verify(jobApplicationRepository, never())
                .save(any(JobApplication.class));
    }
    
    @Test
    void shouldRejectAccessToAnotherUsersApplication() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(jobApplicationRepository
                .findByIdAndUserId(10L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.getApplicationById(10L)
        );
    }
    
    @Test
    void shouldDeleteApplicationSuccessfully() {

        JobApplication application =
                new JobApplication();

        application.setId(10L);
        application.setUser(user);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(jobApplicationRepository
                .findByIdAndUserId(10L, 1L))
                .thenReturn(Optional.of(application));

        jobApplicationService.deleteApplication(10L);

        verify(jobApplicationRepository)
                .delete(application);
    }
    
    @Test
    void shouldNotDeleteAnotherUsersApplication() {

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(jobApplicationRepository
                .findByIdAndUserId(10L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> jobApplicationService.deleteApplication(10L)
        );

        verify(
                jobApplicationRepository,
                never()
        ).delete(any(JobApplication.class));
    }
}