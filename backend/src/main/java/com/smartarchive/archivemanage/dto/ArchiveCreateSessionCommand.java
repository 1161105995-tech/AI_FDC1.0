package com.smartarchive.archivemanage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ArchiveCreateSessionCommand {
    @NotBlank
    private String createMode;
}
