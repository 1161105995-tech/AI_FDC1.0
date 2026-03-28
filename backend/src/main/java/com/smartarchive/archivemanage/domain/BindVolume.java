package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("arc_bind_volume")
public class BindVolume {
    @TableId(type = IdType.AUTO)
    private Long volumeId;
    private Long bindBatchId;
    private String bindVolumeCode;
    private String volumeTitle;
    private String bindRuleKey;
    private String carrierTypeCode;
    private Integer archiveCount;
    private Integer totalPageCount;
    private Integer totalCopyCount;
    private String bindStatus;
    private String remark;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
