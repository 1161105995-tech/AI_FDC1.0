package com.smartarchive.documenttype.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DocumentTypeUpdateCommand {
    @NotBlank(message = "不能为空")
    @Size(max = 128, message = "长度不能超过128")
    private String typeName;

    @Size(max = 500, message = "长度不能超过500")
    private String description;

    @NotBlank(message = "不能为空")
    private String enabledFlag;

    private String parentCode;
}
