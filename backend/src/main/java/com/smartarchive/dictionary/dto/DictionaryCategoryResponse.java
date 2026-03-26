package com.smartarchive.dictionary.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DictionaryCategoryResponse {
    private Long id;
    private String categoryCode;
    private String categoryName;
    private String description;
    private String enabledFlag;
    private int itemCount;
}