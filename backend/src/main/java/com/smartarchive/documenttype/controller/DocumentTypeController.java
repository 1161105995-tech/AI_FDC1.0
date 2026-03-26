package com.smartarchive.documenttype.controller;

import com.smartarchive.common.api.ApiResponse;
import com.smartarchive.documenttype.dto.DocumentTypeCreateCommand;
import com.smartarchive.documenttype.dto.DocumentTypePermissionPreviewResponse;
import com.smartarchive.documenttype.dto.DocumentTypeTreeNodeResponse;
import com.smartarchive.documenttype.dto.DocumentTypeUpdateCommand;
import com.smartarchive.documenttype.service.DocumentTypeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/base-data/document-types")
@RequiredArgsConstructor
public class DocumentTypeController {
    private final DocumentTypeService documentTypeService;

    @GetMapping("/tree")
    public ApiResponse<List<DocumentTypeTreeNodeResponse>> listTree() {
        return ApiResponse.success(documentTypeService.listTree());
    }

    @GetMapping("/permissions/preview")
    public ApiResponse<DocumentTypePermissionPreviewResponse> permissionPreview() {
        return ApiResponse.success(documentTypeService.getPermissionPreview());
    }

    @GetMapping("/{typeCode}")
    public ApiResponse<DocumentTypeTreeNodeResponse> detail(@PathVariable String typeCode) {
        return ApiResponse.success(documentTypeService.getDetail(typeCode));
    }

    @PostMapping
    public ApiResponse<DocumentTypeTreeNodeResponse> create(@Valid @RequestBody DocumentTypeCreateCommand command) {
        return ApiResponse.success(documentTypeService.create(command));
    }

    @PutMapping("/{typeCode}")
    public ApiResponse<DocumentTypeTreeNodeResponse> update(@PathVariable String typeCode,
                                                            @Valid @RequestBody DocumentTypeUpdateCommand command) {
        return ApiResponse.success(documentTypeService.update(typeCode, command));
    }

    @DeleteMapping("/{typeCode}")
    public ApiResponse<Void> delete(@PathVariable String typeCode) {
        documentTypeService.delete(typeCode);
        return ApiResponse.success(null);
    }
}
