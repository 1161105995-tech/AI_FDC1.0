package com.smartarchive.archive.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartarchive.archive.command.CreateInventoryTaskCommand;
import com.smartarchive.archive.domain.InventoryTask;
import java.util.List;

public interface InventoryTaskService extends IService<InventoryTask> {
    List<InventoryTask> listTasks();
    InventoryTask createTask(CreateInventoryTaskCommand command);
}