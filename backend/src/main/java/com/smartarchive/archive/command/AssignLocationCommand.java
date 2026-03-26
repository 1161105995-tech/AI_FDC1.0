package com.smartarchive.archive.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignLocationCommand {
    @NotNull
    private Long archiveObjectId;
    @NotBlank
    private String locationCode;
}
