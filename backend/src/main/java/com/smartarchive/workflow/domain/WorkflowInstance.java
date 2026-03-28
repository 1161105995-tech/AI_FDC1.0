package com.smartarchive.workflow.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("wf_workflow_instance")
public class WorkflowInstance {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String processInstanceId;
    private String businessKey;
    private String processDefinitionId;
    private String processDefinitionKey;
    private String processDefinitionName;
    private String status;
    private String businessType;
    private Long businessId;
    private String initiatorId;
    private String initiatorName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String variables;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}