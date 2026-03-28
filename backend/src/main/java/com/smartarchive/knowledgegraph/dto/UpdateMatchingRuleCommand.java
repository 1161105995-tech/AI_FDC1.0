package com.smartarchive.knowledgegraph.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class UpdateMatchingRuleCommand {
    @NotBlank
    private String ruleName;
    private List<String> primaryKeys;
    private List<String> auxiliaryKeys;
    @NotBlank
    private String matchMode;
    private BigDecimal amountTolerance;
    private Integer timeWindowDays;
    @NotBlank
    private String conflictStrategy;
    private String enabledFlag = "Y";
}
