package com.smartarchive.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SecurityPolicyDto {
    private String policyCode;
    private String name;
    private String category;
    private String status;
}
