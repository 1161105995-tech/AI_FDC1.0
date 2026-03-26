package com.smartarchive.archive.controller;

import com.smartarchive.archive.command.AssignLocationCommand;
import com.smartarchive.archive.domain.ArchiveObject;
import com.smartarchive.archive.dto.ArchiveObjectQueryRequest;
import com.smartarchive.archive.service.ArchiveObjectService;
import com.smartarchive.common.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/archive/objects")
@RequiredArgsConstructor
public class ArchiveObjectController {
    private final ArchiveObjectService archiveObjectService;

    @GetMapping
    public ApiResponse<List<ArchiveObject>> list(@ModelAttribute ArchiveObjectQueryRequest request) {
        return ApiResponse.success(archiveObjectService.search(request));
    }

    @PostMapping("/assign-location")
    public ApiResponse<Boolean> assignLocation(@Valid @RequestBody AssignLocationCommand command) {
        return ApiResponse.success(archiveObjectService.assignLocation(command));
    }
}
