package com.smartarchive.documentorganization.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DocumentOrganizationCreateCommand {
    @NotBlank(message = "cannot be blank")
    @Size(max = 64, message = "length cannot exceed 64")
    private String documentOrganizationCode;

    @NotBlank(message = "cannot be blank")
    @Size(max = 128, message = "length cannot exceed 128")
    private String documentOrganizationName;

    @NotBlank(message = "cannot be blank")
    @Size(max = 500, message = "length cannot exceed 500")
    private String description;

    @NotBlank(message = "cannot be blank")
    @Size(max = 32, message = "length cannot exceed 32")
    private String countryCode;

    @Size(max = 64, message = "length cannot exceed 64")
    private String cityCode;

    @NotBlank(message = "cannot be blank")
    private String enabledFlag;
}
