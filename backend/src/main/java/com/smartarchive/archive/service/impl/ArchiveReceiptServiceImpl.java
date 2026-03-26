package com.smartarchive.archive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartarchive.archive.command.CreateArchiveReceiptCommand;
import com.smartarchive.archive.domain.ArchiveReceipt;
import com.smartarchive.archive.domain.WorkflowInstance;
import com.smartarchive.archive.mapper.ArchiveReceiptMapper;
import com.smartarchive.archive.service.ArchiveReceiptService;
import com.smartarchive.archive.service.CatalogTaskService;
import com.smartarchive.archive.service.WorkflowInstanceService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArchiveReceiptServiceImpl extends ServiceImpl<ArchiveReceiptMapper, ArchiveReceipt> implements ArchiveReceiptService {
    private final WorkflowInstanceService workflowInstanceService;
    private final CatalogTaskService catalogTaskService;

    @Override
    public List<ArchiveReceipt> listReceipts() {
        return list(new LambdaQueryWrapper<ArchiveReceipt>().orderByDesc(ArchiveReceipt::getSubmittedAt));
    }

    @Override
    public ArchiveReceipt createReceipt(CreateArchiveReceiptCommand command) {
        String receiptCode = "REC-" + System.currentTimeMillis();
        WorkflowInstance instance = workflowInstanceService.start("ARC_RECEIVE", receiptCode, "ARCHIVE_RECEIPT", "RECEIVE_REVIEW");

        ArchiveReceipt receipt = new ArchiveReceipt();
        receipt.setReceiptCode(receiptCode);
        receipt.setSourceDept(command.getSourceDept());
        receipt.setArchiveTitle(command.getArchiveTitle());
        receipt.setArchiveType(command.getArchiveType());
        receipt.setSecurityLevel(command.getSecurityLevel());
        receipt.setReceiveStatus("PENDING_REVIEW");
        receipt.setWorkflowInstanceCode(instance.getInstanceCode());
        receipt.setSubmittedBy(command.getSubmittedBy());
        receipt.setSubmittedAt(LocalDateTime.now());
        save(receipt);

        catalogTaskService.createTaskForArchive(receiptCode.replace("REC", "ARC"), command.getArchiveTitle());
        return receipt;
    }
}
