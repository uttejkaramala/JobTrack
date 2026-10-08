package com.jobtrack.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UpcomingInterviewResponse {

    private Long interviewId;

    private Long applicationId;

    private String companyName;

    private String jobTitle;

    private String roundName;

    private LocalDateTime interviewDate;

    private String interviewType;

    private String status;
}