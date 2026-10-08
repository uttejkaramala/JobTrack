package com.jobtrack.dto.dashboard;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardSummaryResponse {

    private long totalApplications;

    private long saved;
    private long applied;
    private long screening;
    private long interview;
    private long offers;
    private long rejected;
    private long withdrawn;
    
    private long overdueFollowUps;
    private long todayFollowUps;
    private long upcomingFollowUps;
    
    
    private List<UpcomingInterviewResponse> upcomingInterviews;
    
    private List<ApplicationsBySourceResponse> applicationsBySource;
    
    
    private List<ApplicationsByWorkModeResponse> applicationsByWorkMode;

    private List<ApplicationsByEmploymentTypeResponse> applicationsByEmploymentType;
}