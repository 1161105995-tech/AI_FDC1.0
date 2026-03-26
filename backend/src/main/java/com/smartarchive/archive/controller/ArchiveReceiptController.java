package com.smartarchive.archive.controller;

import com.smartarchive.archive.command.CreateArchiveReceiptCommand;
import com.smartarchive.archive.domain.ArchiveReceipt;
import com.smartarchive.archive.service.ArchiveReceiptService;
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
@RequestMapping("/api/archive/receipts")
@RequiredArgsConstructor
public class ArchiveReceiptController {
    private final ArchiveReceiptService archiveReceiptService;

    @GetMapping
    public ApiResponse<List<ArchiveReceipt>> list() {
        return ApiResponse.success(archiveReceiptService.listReceipts());
    }

    @PostMapping
    public ApiResponse<ArchiveReceipt> create(@Valid @RequestBody CreateArchiveReceiptCommand command) {
        return ApiResponse.success(archiveReceiptService.createReceipt(command));
    }
}
