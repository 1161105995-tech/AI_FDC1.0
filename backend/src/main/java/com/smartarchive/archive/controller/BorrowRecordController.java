package com.smartarchive.archive.controller;

import com.smartarchive.archive.command.CreateBorrowRecordCommand;
import com.smartarchive.archive.domain.BorrowRecord;
import com.smartarchive.archive.service.BorrowRecordService;
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
@RequestMapping("/api/archive/borrow-records")
@RequiredArgsConstructor
public class BorrowRecordController {
    private final BorrowRecordService borrowRecordService;

    @GetMapping
    public ApiResponse<List<BorrowRecord>> list() {
        return ApiResponse.success(borrowRecordService.listRecords());
    }

    @PostMapping
    public ApiResponse<BorrowRecord> create(@Valid @RequestBody CreateBorrowRecordCommand command) {
        return ApiResponse.success(borrowRecordService.createRecord(command));
    }
}