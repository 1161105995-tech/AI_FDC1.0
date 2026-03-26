package com.smartarchive.archivemanage.controller;

import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldCreateCommand;
import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldResponse;
import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldUpdateCommand;
import com.smartarchive.archivemanage.service.DocumentTypeExtFieldService;
import com.smartarchive.common.api.ApiResponse;
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
@RequestMapping("/api/base-data/document-types/{documentTypeCode}/ext-fields")
@RequiredArgsConstructor
public class DocumentTypeExtFieldController {
    private final DocumentTypeExtFieldService documentTypeExtFieldService;

    @GetMapping
    public ApiResponse<List<DocumentTypeExtFieldResponse>> listDirect(@PathVariable String documentTypeCode) {
        return ApiResponse.success(documentTypeExtFieldService.listDirect(documentTypeCode));
    }

    @GetMapping("/effective")
    public ApiResponse<List<DocumentTypeExtFieldResponse>> listEffective(@PathVariable String documentTypeCode) {
        return ApiResponse.success(documentTypeExtFieldService.listEffective(documentTypeCode));
    }

    @PostMapping
    public ApiResponse<DocumentTypeExtFieldResponse> create(@PathVariable String documentTypeCode,
                                                            @Valid @RequestBody DocumentTypeExtFieldCreateCommand command) {
        return ApiResponse.success(documentTypeExtFieldService.create(documentTypeCode, command));
    }

    @PutMapping("/{fieldCode}")
    public ApiResponse<DocumentTypeExtFieldResponse> update(@PathVariable String documentTypeCode,
                                                            @PathVariable String fieldCode,
                                                            @Valid @RequestBody DocumentTypeExtFieldUpdateCommand command) {
        return ApiResponse.success(documentTypeExtFieldService.update(documentTypeCode, fieldCode, command));
    }

    @DeleteMapping("/{fieldCode}")
    public ApiResponse<Void> delete(@PathVariable String documentTypeCode, @PathVariable String fieldCode) {
        documentTypeExtFieldService.delete(documentTypeCode, fieldCode);
        return ApiResponse.success(null);
    }
}
