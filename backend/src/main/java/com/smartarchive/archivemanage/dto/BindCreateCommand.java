package com.smartarchive.archivemanage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class BindCreateCommand {
    @NotBlank
    private String bindMode;
    private String bindRemark;
    @NotEmpty
    private List<BindCreateVolumeCommand> volumes;

    @Data
    public static class BindCreateVolumeCommand {
        private String volumeTitle;
        private String bindRuleKey;
        private String carrierTypeCode;
        private String remark;
        @NotEmpty
        private List<BindCreateVolumeItemCommand> items;
    }

    @Data
    public static class BindCreateVolumeItemCommand {
        private Long archiveId;
        private Integer sortNo;
        private String primaryFlag;
        private String bindReason;
    }
}
