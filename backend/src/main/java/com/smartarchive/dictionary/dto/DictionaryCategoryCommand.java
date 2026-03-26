package com.smartarchive.dictionary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DictionaryCategoryCommand {
    @NotBlank
    private String categoryCode;
    @NotBlank
    private String categoryName;
    private String description;
    private String enabledFlag = "Y";
}