package com.smartarchive.documentorganization.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentOrganizationPermissionPreviewResponse {
    private String moduleCode;
    private String moduleName;
    private List<PermissionPoint> permissionPoints;
    private List<DataDimension> dataDimensions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionPoint {
        private String code;
        private String name;
        private String action;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataDimension {
        private String code;
        private String name;
        private String description;
    }
}
