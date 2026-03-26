package com.smartarchive.common.audit.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("com_operation_audit")
public class OperationAuditRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String moduleCode;
    private String moduleName;
    private String businessType;
    private String businessKey;
    private String operationType;
    private String operationSummary;
    private String beforeSnapshot;
    private String afterSnapshot;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime operationTime;
}
