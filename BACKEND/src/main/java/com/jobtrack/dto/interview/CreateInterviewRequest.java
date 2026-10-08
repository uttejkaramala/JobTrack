package com.jobtrack.dto.interview;

import java.time.LocalDateTime;

import com.jobtrack.entity.InterviewType;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateInterviewRequest {

    @NotBlank(message = "Round name is required")
    @Size(max = 100, message = "Round name must not exceed 100 characters")
    private String roundName;

    @NotNull(message = "Interview date is required")
    @Future(message = "Interview date must be in the future")
    private LocalDateTime interviewDate;

    @NotNull(message = "Interview type is required")
    private InterviewType interviewType;

    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;
}