package com.smartarchive.archive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartarchive.archive.command.CreateDisposalRecordCommand;
import com.smartarchive.archive.domain.DisposalRecord;
import com.smartarchive.archive.mapper.DisposalRecordMapper;
import com.smartarchive.archive.service.DisposalRecordService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DisposalRecordServiceImpl extends ServiceImpl<DisposalRecordMapper, DisposalRecord> implements DisposalRecordService {
    @Override
    public List<DisposalRecord> listRecords() {
        return list(new LambdaQueryWrapper<DisposalRecord>().orderByDesc(DisposalRecord::getCreatedAt));
    }

    @Override
    public DisposalRecord createRecord(CreateDisposalRecordCommand command) {
        DisposalRecord record = new DisposalRecord();
        record.setDisposalCode("DSP-" + System.currentTimeMillis());
        record.setArchiveCode(command.getArchiveCode());
        record.setArchiveTitle(command.getArchiveTitle());
        record.setRetentionPeriod(command.getRetentionPeriod());
        record.setAppraisalConclusion(command.getAppraisalConclusion());
        record.setApprovalStatus("PENDING");
        record.setDisposalStatus("UNDER_APPRAISAL");
        record.setCreatedAt(LocalDateTime.now());
        save(record);
        return record;
    }
}