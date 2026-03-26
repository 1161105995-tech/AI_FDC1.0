package com.smartarchive.archive.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDisposalRecordCommand {
    @NotBlank
    private String archiveCode;
    @NotBlank
    private String archiveTitle;
    @NotBlank
    private String retentionPeriod;
    @NotBlank
    private String appraisalConclusion;
}