package com.jobtrack.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jobtrack.dto.interview.CreateInterviewRequest;
import com.jobtrack.dto.interview.InterviewResponse;
import com.jobtrack.entity.Interview;
import com.jobtrack.entity.InterviewStatus;
import com.jobtrack.entity.InterviewType;
import com.jobtrack.entity.JobApplication;
import com.jobtrack.entity.User;
import com.jobtrack.exception.ResourceNotFoundException;
import com.jobtrack.repository.InterviewRepository;
import com.jobtrack.repository.JobApplicationRepository;

@ExtendWith(MockitoExtension.class)
class InterviewServiceTest {

    @Mock
    private InterviewRepository interviewRepository;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private InterviewService interviewService;

    private User user;
    private JobApplication application;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        application = new JobApplication();
        application.setId(10L);
        application.setCompanyName("Google");
        application.setJobTitle("Java Developer");
        application.setUser(user);
    }

    @Test
    void shouldCreateInterviewSuccessfully() {

        CreateInterviewRequest request =
                new CreateInterviewRequest();

        request.setRoundName("Technical Interview");
        request.setInterviewDate(
                LocalDateTime.now().plusDays(2)
        );
        request.setInterviewType(InterviewType.ONLINE);
        request.setNotes("Prepare Spring Boot questions");

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(jobApplicationRepository.findByIdAndUserId(10L, 1L))
                .thenReturn(Optional.of(application));

        when(interviewRepository.save(any(Interview.class)))
                .thenAnswer(invocation -> {

                    Interview interview =
                            invocation.getArgument(0);

                    interview.setId(100L);

                    return interview;
                });

        InterviewResponse response =
                interviewService.createInterview(10L, request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(
                "Technical Interview",
                response.getRoundName()
        );
        assertEquals(
                InterviewType.ONLINE,
                response.getInterviewType()
        );
        assertEquals(
                InterviewStatus.SCHEDULED,
                response.getStatus()
        );
        assertEquals(
                "Prepare Spring Boot questions",
                response.getNotes()
        );

        verify(currentUserService)
                .getCurrentUser();

        verify(jobApplicationRepository)
                .findByIdAndUserId(10L, 1L);

        verify(interviewRepository)
                .save(any(Interview.class));
    }

    @Test
    void shouldNotCreateInterviewForAnotherUsersApplication() {

        CreateInterviewRequest request =
                new CreateInterviewRequest();

        request.setRoundName("Technical Interview");
        request.setInterviewDate(
                LocalDateTime.now().plusDays(2)
        );
        request.setInterviewType(InterviewType.ONLINE);

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(jobApplicationRepository.findByIdAndUserId(10L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> interviewService.createInterview(10L, request)
        );

        verify(interviewRepository, never())
                .save(any(Interview.class));
    }
}