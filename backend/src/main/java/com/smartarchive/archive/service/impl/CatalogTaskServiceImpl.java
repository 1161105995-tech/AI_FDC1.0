package com.smartarchive.archive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartarchive.archive.domain.CatalogTask;
import com.smartarchive.archive.mapper.CatalogTaskMapper;
import com.smartarchive.archive.service.CatalogTaskService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CatalogTaskServiceImpl extends ServiceImpl<CatalogTaskMapper, CatalogTask> implements CatalogTaskService {
    @Override
    public List<CatalogTask> listTasks() {
        return list(new LambdaQueryWrapper<CatalogTask>().orderByAsc(CatalogTask::getDueDate));
    }

    @Override
    public void createTaskForArchive(String archiveCode, String archiveTitle) {
        CatalogTask task = new CatalogTask();
        task.setTaskCode("CAT-" + System.currentTimeMillis());
        task.setArchiveCode(archiveCode);
        task.setArchiveTitle(archiveTitle);
        task.setTaskStatus("PENDING");
        task.setAssignee("档案管理员");
        task.setDueDate(LocalDate.now().plusDays(3));
        save(task);
    }
}
