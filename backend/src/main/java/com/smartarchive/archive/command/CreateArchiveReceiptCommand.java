package com.smartarchive.archive.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateArchiveReceiptCommand {
    @NotBlank
    private String sourceDept;
    @NotBlank
    private String archiveTitle;
    @NotBlank
    private String archiveType;
    @NotBlank
    private String securityLevel;
    @NotBlank
    private String submittedBy;
}
