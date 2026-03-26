package com.smartarchive.companyproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrgCategoryDictionaryCommand {
    @NotBlank(message = "组织类别编码不能为空")
    @Size(max = 64, message = "组织类别编码长度不能超过64")
    private String categoryCode;

    @NotBlank(message = "组织类别名称不能为空")
    @Size(max = 128, message = "组织类别名称长度不能超过128")
    private String categoryName;

    @NotNull(message = "排序不能为空")
    private Integer sortOrder;

    @NotBlank(message = "启用标识不能为空")
    private String enabledFlag;
}