package com.smartarchive.archivemanage.controller;

import com.smartarchive.archivemanage.dto.AiModelConfigResponse;
import com.smartarchive.archivemanage.dto.ArchiveAskCommand;
import com.smartarchive.archivemanage.dto.ArchiveAskResponse;
import com.smartarchive.archivemanage.dto.ArchiveAttachmentResponse;
import com.smartarchive.archivemanage.dto.ArchiveAttachmentUpdateCommand;
import com.smartarchive.archivemanage.dto.ArchiveCreateCommand;
import com.smartarchive.archivemanage.dto.ArchiveCreateOptionsResponse;
import com.smartarchive.archivemanage.dto.ArchiveCreateSessionCommand;
import com.smartarchive.archivemanage.dto.ArchiveCreateSessionResponse;
import com.smartarchive.archivemanage.dto.ArchiveDefaultResolveResponse;
import com.smartarchive.archivemanage.dto.ArchiveQueryCommand;
import com.smartarchive.archivemanage.dto.ArchiveQueryResponse;
import com.smartarchive.archivemanage.dto.ArchiveSummaryResponse;
import com.smartarchive.archivemanage.service.ArchiveManagementService;
import com.smartarchive.common.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/archive-management")
@RequiredArgsConstructor
public class ArchiveManagementController {
    private final ArchiveManagementService archiveManagementService;

    @GetMapping("/create/options")
    public ApiResponse<ArchiveCreateOptionsResponse> loadCreateOptions() {
        return ApiResponse.success(archiveManagementService.loadCreateOptions());
    }

    @GetMapping("/create/defaults")
    public ApiResponse<ArchiveDefaultResolveResponse> resolveDefaults(@RequestParam String companyProjectCode,
                                                                      @RequestParam String documentTypeCode,
                                                                      @RequestParam(required = false) String customRule,
                                                                      @RequestParam(required = false) String archiveDestination) {
        return ApiResponse.success(archiveManagementService.resolveDefaults(companyProjectCode, documentTypeCode, customRule, archiveDestination));
    }

    @PostMapping("/create/sessions")
    public ApiResponse<ArchiveCreateSessionResponse> createSession(@Valid @RequestBody ArchiveCreateSessionCommand command) {
        return ApiResponse.success(archiveManagementService.createSession(command));
    }

    @GetMapping("/create/sessions/{sessionCode}")
    public ApiResponse<ArchiveCreateSessionResponse> getSession(@PathVariable String sessionCode) {
        return ApiResponse.success(archiveManagementService.getSession(sessionCode));
    }

    @PostMapping("/create/sessions/{sessionCode}/attachments")
    public ApiResponse<ArchiveAttachmentResponse> uploadAttachment(@PathVariable String sessionCode,
                                                                   @RequestParam String attachmentRole,
                                                                   @RequestParam(required = false) String attachmentTypeCode,
                                                                   @RequestParam(required = false) String remark,
                                                                   @RequestParam("file") MultipartFile file) {
        return ApiResponse.success(archiveManagementService.uploadAttachment(sessionCode, attachmentRole, attachmentTypeCode, remark, file));
    }

    @PutMapping("/create/sessions/{sessionCode}/attachments/{attachmentId}")
    public ApiResponse<ArchiveAttachmentResponse> updateAttachment(@PathVariable String sessionCode,
                                                                   @PathVariable Long attachmentId,
                                                                   @RequestBody ArchiveAttachmentUpdateCommand command) {
        return ApiResponse.success(archiveManagementService.updateAttachment(sessionCode, attachmentId, command));
    }

    @PostMapping("/create/archives")
    public ApiResponse<ArchiveSummaryResponse> createArchive(@RequestBody ArchiveCreateCommand command) {
        return ApiResponse.success(archiveManagementService.createArchive(command));
    }

    @PostMapping("/create/query")
    public ApiResponse<ArchiveQueryResponse> queryArchives(@RequestBody ArchiveQueryCommand command) {
        return ApiResponse.success(archiveManagementService.queryArchives(command));
    }

    @PostMapping("/create/ask")
    public ApiResponse<ArchiveAskResponse> ask(@Valid @RequestBody ArchiveAskCommand command) {
        return ApiResponse.success(archiveManagementService.ask(command));
    }

    @GetMapping("/ai-models")
    public ApiResponse<List<AiModelConfigResponse>> listAiModels() {
        return ApiResponse.success(archiveManagementService.listAiModels());
    }
}
