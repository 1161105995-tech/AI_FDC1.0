package com.smartarchive.rule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RuleDefinitionDto {
    private String code;
    private String name;
    private String category;
    private String expression;
}
