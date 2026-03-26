package com.smartarchive.archive.controller;

import com.smartarchive.archive.domain.CatalogTask;
import com.smartarchive.archive.service.CatalogTaskService;
import com.smartarchive.common.api.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/archive/catalog-tasks")
@RequiredArgsConstructor
public class CatalogTaskController {
    private final CatalogTaskService catalogTaskService;

    @GetMapping
    public ApiResponse<List<CatalogTask>> list() {
        return ApiResponse.success(catalogTaskService.listTasks());
    }
}
