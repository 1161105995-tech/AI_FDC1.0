package com.smartarchive.dictionary.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DictionaryItemResponse {
    private Long id;
    private String categoryCode;
    private String itemCode;
    private String itemName;
    private String itemValue;
    private Integer sortOrder;
    private String enabledFlag;
}