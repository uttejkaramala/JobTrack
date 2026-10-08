package com.jobtrack.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jobtrack.dto.note.CreateNoteRequest;
import com.jobtrack.dto.note.NoteResponse;
import com.jobtrack.entity.ApplicationNote;
import com.jobtrack.entity.JobApplication;
import com.jobtrack.entity.User;
import com.jobtrack.exception.ResourceNotFoundException;
import com.jobtrack.repository.ApplicationNoteRepository;
import com.jobtrack.repository.JobApplicationRepository;

@ExtendWith(MockitoExtension.class)
class ApplicationNoteServiceTest {

    @Mock
    private ApplicationNoteRepository noteRepository;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private ApplicationNoteService noteService;

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
    void shouldCreateNoteSuccessfully() {

        CreateNoteRequest request = new CreateNoteRequest();
        request.setContent("Prepare Spring Boot questions");

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(jobApplicationRepository.findByIdAndUserId(10L, 1L))
                .thenReturn(Optional.of(application));

        when(noteRepository.save(any(ApplicationNote.class)))
                .thenAnswer(invocation -> {

                    ApplicationNote note =
                            invocation.getArgument(0);

                    note.setId(100L);

                    return note;
                });

        NoteResponse response =
                noteService.createNote(10L, request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(
                "Prepare Spring Boot questions",
                response.getContent()
        );

        verify(currentUserService)
                .getCurrentUser();

        verify(jobApplicationRepository)
                .findByIdAndUserId(10L, 1L);

        verify(noteRepository)
                .save(any(ApplicationNote.class));
    }

    @Test
    void shouldNotCreateNoteForAnotherUsersApplication() {

        CreateNoteRequest request = new CreateNoteRequest();
        request.setContent("Some private note");

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(jobApplicationRepository.findByIdAndUserId(10L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> noteService.createNote(10L, request)
        );

        verify(noteRepository, never())
                .save(any(ApplicationNote.class));
    }
}