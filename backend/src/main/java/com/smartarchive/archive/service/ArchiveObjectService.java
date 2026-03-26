package com.smartarchive.archive.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartarchive.archive.command.AssignLocationCommand;
import com.smartarchive.archive.domain.ArchiveObject;
import com.smartarchive.archive.dto.ArchiveObjectQueryRequest;
import java.util.List;

public interface ArchiveObjectService extends IService<ArchiveObject> {
    List<ArchiveObject> search(ArchiveObjectQueryRequest request);
    boolean assignLocation(AssignLocationCommand command);
}
