package com.smartarchive.knowledgegraph.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchingRuleVersionResponse {
    private Long versionId;
    private String scenarioCode;
    private String ruleName;
    private List<String> primaryKeys;
    private List<String> auxiliaryKeys;
    private String matchMode;
    private BigDecimal amountTolerance;
    private Integer timeWindowDays;
    private String conflictStrategy;
    private Integer ruleVersion;
    private String currentFlag;
    private String enabledFlag;
    private LocalDateTime lastUpdateDate;
}
