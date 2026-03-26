package com.smartarchive.dictionary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DictionaryItemUpdateCommand {
    @NotBlank
    private String itemName;
    private String itemValue;
    private Integer sortOrder = 1;
    private String enabledFlag = "Y";
}