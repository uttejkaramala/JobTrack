package com.jobtrack.dto.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateNoteRequest {

    @NotBlank(message = "Note content is required")
    @Size(max = 5000, message = "Note must not exceed 5000 characters")
    private String content;
}