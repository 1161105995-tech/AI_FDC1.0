package com.smartarchive.archive.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartarchive.archive.command.CreateArchiveReceiptCommand;
import com.smartarchive.archive.domain.ArchiveReceipt;
import java.util.List;

public interface ArchiveReceiptService extends IService<ArchiveReceipt> {
    List<ArchiveReceipt> listReceipts();
    ArchiveReceipt createReceipt(CreateArchiveReceiptCommand command);
}
