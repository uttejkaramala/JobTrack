package com.jobtrack.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jobtrack.dto.interview.CreateInterviewRequest;
import com.jobtrack.dto.interview.InterviewResponse;
import com.jobtrack.dto.interview.UpdateInterviewRequest;
import com.jobtrack.entity.InterviewStatus;
import com.jobtrack.service.InterviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(
            InterviewService interviewService) {

        this.interviewService = interviewService;
    }


    // ---------------------------------------------------------
    // CREATE INTERVIEW
    // POST /api/applications/{applicationId}/interviews
    // ---------------------------------------------------------

    @PostMapping(
            "/applications/{applicationId}/interviews")
    public ResponseEntity<InterviewResponse> createInterview(

            @PathVariable Long applicationId,

            @Valid
            @RequestBody CreateInterviewRequest request) {

        InterviewResponse response =
                interviewService.createInterview(
                        applicationId,
                        request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // ---------------------------------------------------------
    // GET APPLICATION INTERVIEWS
    // GET /api/applications/{applicationId}/interviews
    // ---------------------------------------------------------

    @GetMapping(
            "/applications/{applicationId}/interviews")
    public ResponseEntity<List<InterviewResponse>>
            getInterviewsForApplication(

                    @PathVariable Long applicationId) {

        List<InterviewResponse> responses =
                interviewService
                        .getInterviewsForApplication(
                                applicationId);

        return ResponseEntity.ok(responses);
    }


    // ---------------------------------------------------------
    // GET ONE INTERVIEW
    // GET /api/interviews/{id}
    // ---------------------------------------------------------

    @GetMapping("/interviews/{id}")
    public ResponseEntity<InterviewResponse>
            getInterviewById(
                    @PathVariable Long id) {

        InterviewResponse response =
                interviewService.getInterviewById(id);

        return ResponseEntity.ok(response);
    }


    // ---------------------------------------------------------
    // UPDATE INTERVIEW
    // PUT /api/interviews/{id}
    // ---------------------------------------------------------

    @PutMapping("/interviews/{id}")
    public ResponseEntity<InterviewResponse>
            updateInterview(

                    @PathVariable Long id,

                    @Valid
                    @RequestBody UpdateInterviewRequest request) {

        InterviewResponse response =
                interviewService.updateInterview(
                        id,
                        request);

        return ResponseEntity.ok(response);
    }


    // ---------------------------------------------------------
    // DELETE INTERVIEW
    // DELETE /api/interviews/{id}
    // ---------------------------------------------------------

    @DeleteMapping("/interviews/{id}")
    public ResponseEntity<Void> deleteInterview(
            @PathVariable Long id) {

        interviewService.deleteInterview(id);

        return ResponseEntity.noContent().build();
    }


    // ---------------------------------------------------------
    // UPDATE INTERVIEW STATUS
    // PATCH /api/interviews/{id}/status
    // ---------------------------------------------------------

    @PatchMapping("/interviews/{id}/status")
    public ResponseEntity<InterviewResponse>
            updateInterviewStatus(

                    @PathVariable Long id,

                    @RequestParam InterviewStatus status) {

        InterviewResponse response =
                interviewService.updateInterviewStatus(
                        id,
                        status);

        return ResponseEntity.ok(response);
    }
}