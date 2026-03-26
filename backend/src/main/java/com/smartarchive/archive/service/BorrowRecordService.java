package com.smartarchive.archive.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartarchive.archive.command.CreateBorrowRecordCommand;
import com.smartarchive.archive.domain.BorrowRecord;
import java.util.List;

public interface BorrowRecordService extends IService<BorrowRecord> {
    List<BorrowRecord> listRecords();
    BorrowRecord createRecord(CreateBorrowRecordCommand command);
}