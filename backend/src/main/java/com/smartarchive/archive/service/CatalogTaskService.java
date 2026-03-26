package com.smartarchive.archive.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartarchive.archive.domain.CatalogTask;
import java.util.List;

public interface CatalogTaskService extends IService<CatalogTask> {
    List<CatalogTask> listTasks();
    void createTaskForArchive(String archiveCode, String archiveTitle);
}
