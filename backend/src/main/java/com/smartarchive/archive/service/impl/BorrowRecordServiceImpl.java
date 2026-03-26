package com.smartarchive.archive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartarchive.archive.command.CreateBorrowRecordCommand;
import com.smartarchive.archive.domain.BorrowRecord;
import com.smartarchive.archive.mapper.BorrowRecordMapper;
import com.smartarchive.archive.service.BorrowRecordService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class BorrowRecordServiceImpl extends ServiceImpl<BorrowRecordMapper, BorrowRecord> implements BorrowRecordService {
    @Override
    public List<BorrowRecord> listRecords() {
        return list(new LambdaQueryWrapper<BorrowRecord>().orderByDesc(BorrowRecord::getBorrowedAt));
    }

    @Override
    public BorrowRecord createRecord(CreateBorrowRecordCommand command) {
        BorrowRecord record = new BorrowRecord();
        record.setBorrowCode("BOR-" + System.currentTimeMillis());
        record.setArchiveCode(command.getArchiveCode());
        record.setArchiveTitle(command.getArchiveTitle());
        record.setBorrower(command.getBorrower());
        record.setBorrowType(command.getBorrowType());
        record.setApprovalStatus("PENDING");
        record.setBorrowStatus("APPLYING");
        record.setExpectedReturnDate(command.getExpectedReturnDate());
        record.setBorrowedAt(LocalDateTime.now());
        save(record);
        return record;
    }
}