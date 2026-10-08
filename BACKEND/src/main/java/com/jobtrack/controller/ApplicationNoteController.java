package com.jobtrack.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jobtrack.dto.note.CreateNoteRequest;
import com.jobtrack.dto.note.NoteResponse;
import com.jobtrack.dto.note.UpdateNoteRequest;
import com.jobtrack.service.ApplicationNoteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ApplicationNoteController {

    private final ApplicationNoteService noteService;

    public ApplicationNoteController(
            ApplicationNoteService noteService) {

        this.noteService = noteService;
    }

    // CREATE
    // POST /api/applications/{applicationId}/notes

    @PostMapping(
            "/applications/{applicationId}/notes")
    public ResponseEntity<NoteResponse> createNote(

            @PathVariable Long applicationId,

            @Valid
            @RequestBody CreateNoteRequest request) {

        NoteResponse response =
                noteService.createNote(
                        applicationId,
                        request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET ALL
    // GET /api/applications/{applicationId}/notes

    @GetMapping(
            "/applications/{applicationId}/notes")
    public ResponseEntity<List<NoteResponse>>
            getNotesForApplication(

                    @PathVariable Long applicationId) {

        List<NoteResponse> notes =
                noteService.getNotesForApplication(
                        applicationId);

        return ResponseEntity.ok(notes);
    }

    // UPDATE
    // PUT /api/notes/{id}

    @PutMapping("/notes/{id}")
    public ResponseEntity<NoteResponse> updateNote(

            @PathVariable Long id,

            @Valid
            @RequestBody UpdateNoteRequest request) {

        NoteResponse response =
                noteService.updateNote(
                        id,
                        request);

        return ResponseEntity.ok(response);
    }

    // DELETE
    // DELETE /api/notes/{id}

    @DeleteMapping("/notes/{id}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long id) {

        noteService.deleteNote(id);

        return ResponseEntity.noContent().build();
    }
}