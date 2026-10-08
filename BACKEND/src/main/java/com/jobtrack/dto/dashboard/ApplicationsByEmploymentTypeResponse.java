package com.jobtrack.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplicationsByEmploymentTypeResponse {

    private String employmentType;
    private long count;
}