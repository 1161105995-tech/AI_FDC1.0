package com.smartarchive.archive.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("wf_workflow_instance")
public class WorkflowInstance {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String instanceCode;
    private String definitionCode;
    private String businessKey;
    private String businessType;
    private String currentNode;
    private String status;
    private LocalDateTime startedAt;
}
