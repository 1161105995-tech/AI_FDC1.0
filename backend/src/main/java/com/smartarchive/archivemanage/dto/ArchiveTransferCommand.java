package com.smartarchive.archivemanage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class ArchiveTransferCommand {
    @NotEmpty
    private List<Long> archiveIds;
    @NotBlank
    private String assigneeId;
    private String assigneeName;
    @NotBlank
    private String transferMethod;
    private String logisticsCompany;
    private String trackingNumber;
    private String remark;
    private String initiatorId;
    private String initiatorName;
}
