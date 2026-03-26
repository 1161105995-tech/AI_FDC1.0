package com.smartarchive.companyproject.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompanyProjectLineCommand {
    @NotBlank(message = "组织类别不能为空")
    @Size(max = 64, message = "组织类别长度不能超过64")
    private String orgCategory;

    @NotBlank(message = "组织编码不能为空")
    @Size(max = 64, message = "组织编码长度不能超过64")
    private String organizationCode;

    @NotBlank(message = "组织名称不能为空")
    @Size(max = 128, message = "组织名称长度不能超过128")
    private String organizationName;

    @NotBlank(message = "有效标识不能为空")
    private String validFlag;
}
