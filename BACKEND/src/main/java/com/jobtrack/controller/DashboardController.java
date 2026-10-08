package com.jobtrack.controller;

import com.jobtrack.dto.dashboard.DashboardSummaryResponse;
import com.jobtrack.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {

        DashboardSummaryResponse response =
                dashboardService.getSummary();

        return ResponseEntity.ok(response);
    }
}