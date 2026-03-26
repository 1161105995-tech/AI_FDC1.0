package com.smartarchive.archive.controller;

import com.smartarchive.archive.command.CreateInventoryTaskCommand;
import com.smartarchive.archive.domain.InventoryTask;
import com.smartarchive.archive.service.InventoryTaskService;
import com.smartarchive.common.api.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/archive/inventory-tasks")
@RequiredArgsConstructor
public class InventoryTaskController {
    private final InventoryTaskService inventoryTaskService;

    @GetMapping
    public ApiResponse<List<InventoryTask>> list() {
        return ApiResponse.success(inventoryTaskService.listTasks());
    }

    @PostMapping
    public ApiResponse<InventoryTask> create(@Valid @RequestBody CreateInventoryTaskCommand command) {
        return ApiResponse.success(inventoryTaskService.createTask(command));
    }
}