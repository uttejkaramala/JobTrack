package com.jobtrack.dto.interview;

import java.time.LocalDateTime;

import com.jobtrack.entity.InterviewStatus;
import com.jobtrack.entity.InterviewType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InterviewResponse {

    private Long id;
    private String roundName;
    private LocalDateTime interviewDate;
    private InterviewType interviewType;
    private InterviewStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}