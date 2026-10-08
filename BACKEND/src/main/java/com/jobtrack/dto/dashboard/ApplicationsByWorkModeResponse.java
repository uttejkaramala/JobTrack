package com.jobtrack.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplicationsByWorkModeResponse {

    private String workMode;
    private long count;
}