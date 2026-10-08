package com.jobtrack.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplicationsBySourceResponse {

    private String source;

    private long count;
}