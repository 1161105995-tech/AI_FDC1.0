package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("arc_bind_batch")
public class BindBatch {
    @TableId(type = IdType.AUTO)
    private Long bindBatchId;
    private String bindBatchCode;
    private String bindMode;
    private String sourceType;
    private String bindStatus;
    private String bindRemark;
    private String guidedStorageFlag;
    private Integer volumeCount;
    private Integer archiveCount;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
