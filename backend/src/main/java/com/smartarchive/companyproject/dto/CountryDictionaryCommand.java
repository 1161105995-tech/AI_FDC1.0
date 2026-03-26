package com.smartarchive.companyproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CountryDictionaryCommand {
    @NotBlank(message = "国家编码不能为空")
    @Size(max = 32, message = "国家编码长度不能超过32")
    private String countryCode;

    @NotBlank(message = "国家名称不能为空")
    @Size(max = 128, message = "国家名称长度不能超过128")
    private String countryName;

    @NotNull(message = "排序不能为空")
    private Integer sortOrder;

    @NotBlank(message = "启用标识不能为空")
    private String enabledFlag;
}