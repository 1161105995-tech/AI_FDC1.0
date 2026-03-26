package com.smartarchive.archive.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartarchive.archive.command.CreateInventoryTaskCommand;
import com.smartarchive.archive.domain.InventoryTask;
import com.smartarchive.archive.mapper.InventoryTaskMapper;
import com.smartarchive.archive.service.InventoryTaskService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class InventoryTaskServiceImpl extends ServiceImpl<InventoryTaskMapper, InventoryTask> implements InventoryTaskService {
    @Override
    public List<InventoryTask> listTasks() {
        return list(new LambdaQueryWrapper<InventoryTask>().orderByAsc(InventoryTask::getDueDate));
    }

    @Override
    public InventoryTask createTask(CreateInventoryTaskCommand command) {
        InventoryTask task = new InventoryTask();
        task.setTaskCode("INV-" + System.currentTimeMillis());
        task.setWarehouseCode(command.getWarehouseCode());
        task.setInventoryScope(command.getInventoryScope());
        task.setTaskStatus("PENDING");
        task.setAbnormalCount(0);
        task.setOwner(command.getOwner());
        task.setDueDate(command.getDueDate());
        save(task);
        return task;
    }
}