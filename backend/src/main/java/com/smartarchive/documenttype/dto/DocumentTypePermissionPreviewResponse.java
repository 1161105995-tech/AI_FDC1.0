package com.smartarchive.documenttype.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentTypePermissionPreviewResponse {
    private String moduleCode;
    private String moduleName;
    private List<PermissionPoint> permissionPoints;
    private List<DataDimension> dataDimensions;

    @Data
    @Builder
    public static class PermissionPoint {
        private String code;
        private String name;
        private String action;
        private String description;
    }

    @Data
    @Builder
    public static class DataDimension {
        private String code;
        private String name;
        private String description;
    }
}
