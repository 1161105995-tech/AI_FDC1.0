package com.smartarchive.archive.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartarchive.archive.command.CreateDisposalRecordCommand;
import com.smartarchive.archive.domain.DisposalRecord;
import java.util.List;

public interface DisposalRecordService extends IService<DisposalRecord> {
    List<DisposalRecord> listRecords();
    DisposalRecord createRecord(CreateDisposalRecordCommand command);
}