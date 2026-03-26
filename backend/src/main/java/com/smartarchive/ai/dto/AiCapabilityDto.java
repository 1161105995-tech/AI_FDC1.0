package com.smartarchive.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AiCapabilityDto {
    private String code;
    private String name;
    private String status;
    private String description;
}
