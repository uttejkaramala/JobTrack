package com.jobtrack.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jobtrack.dto.note.CreateNoteRequest;
import com.jobtrack.dto.note.NoteResponse;
import com.jobtrack.dto.note.UpdateNoteRequest;
import com.jobtrack.entity.ApplicationNote;
import com.jobtrack.entity.JobApplication;
import com.jobtrack.entity.User;
import com.jobtrack.exception.ResourceNotFoundException;
import com.jobtrack.repository.ApplicationNoteRepository;
import com.jobtrack.repository.JobApplicationRepository;

@Service
public class ApplicationNoteService {

    private final ApplicationNoteRepository noteRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final CurrentUserService currentUserService;

    public ApplicationNoteService(
            ApplicationNoteRepository noteRepository,
            JobApplicationRepository jobApplicationRepository,
            CurrentUserService currentUserService) {

        this.noteRepository = noteRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.currentUserService = currentUserService;
    }

    // CREATE NOTE

    public NoteResponse createNote(
            Long applicationId,
            CreateNoteRequest request) {

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

        ApplicationNote note = new ApplicationNote();

        note.setContent(request.getContent());
        note.setApplication(application);

        ApplicationNote savedNote =
                noteRepository.save(note);

        return mapToResponse(savedNote);
    }

    // GET ALL NOTES FOR APPLICATION

    public List<NoteResponse> getNotesForApplication(
            Long applicationId) {

        User currentUser =
                currentUserService.getCurrentUser();

        // Verify ownership first
        jobApplicationRepository
                .findByIdAndUserId(
                        applicationId,
                        currentUser.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Job application not found with id: "
                                        + applicationId));

        List<ApplicationNote> notes =
                noteRepository.findByApplicationId(
                        applicationId);

        return notes.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // UPDATE NOTE

    public NoteResponse updateNote(
            Long id,
            UpdateNoteRequest request) {

        User currentUser =
                currentUserService.getCurrentUser();

        ApplicationNote note =
                noteRepository
                        .findByIdAndApplicationUserId(
                                id,
                                currentUser.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Note not found with id: "
                                                + id));

        note.setContent(request.getContent());

        ApplicationNote updatedNote =
                noteRepository.save(note);

        return mapToResponse(updatedNote);
    }

    // DELETE NOTE

    public void deleteNote(Long id) {

        User currentUser =
                currentUserService.getCurrentUser();

        ApplicationNote note =
                noteRepository
                        .findByIdAndApplicationUserId(
                                id,
                                currentUser.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Note not found with id: "
                                                + id));

        noteRepository.delete(note);
    }

    // ENTITY → DTO

    private NoteResponse mapToResponse(
            ApplicationNote note) {

        return new NoteResponse(
                note.getId(),
                note.getContent(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }
}