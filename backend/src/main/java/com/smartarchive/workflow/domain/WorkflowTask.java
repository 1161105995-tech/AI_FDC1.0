package com.smartarchive.workflow.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("wf_workflow_task")
public class WorkflowTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskId;
    private String processInstanceId;
    private String taskDefinitionKey;
    private String taskName;
    private String assignee;
    @TableField(exist = false)
    private String candidateUsers;
    @TableField(exist = false)
    private String candidateGroups;
    private String status;
    private String businessKey;
    @TableField(exist = false)
    private String variables;
    private LocalDateTime createTime;
    @TableField(exist = false)
    private LocalDateTime claimTime;
    private LocalDateTime completeTime;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
