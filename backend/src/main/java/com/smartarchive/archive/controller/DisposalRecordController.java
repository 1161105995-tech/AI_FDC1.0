package com.smartarchive.archive.controller;

import com.smartarchive.archive.command.CreateDisposalRecordCommand;
import com.smartarchive.archive.domain.DisposalRecord;
import com.smartarchive.archive.service.DisposalRecordService;
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
@RequestMapping("/api/archive/disposal-records")
@RequiredArgsConstructor
public class DisposalRecordController {
    private final DisposalRecordService disposalRecordService;

    @GetMapping
    public ApiResponse<List<DisposalRecord>> list() {
        return ApiResponse.success(disposalRecordService.listRecords());
    }

    @PostMapping
    public ApiResponse<DisposalRecord> create(@Valid @RequestBody CreateDisposalRecordCommand command) {
        return ApiResponse.success(disposalRecordService.createRecord(command));
    }
}