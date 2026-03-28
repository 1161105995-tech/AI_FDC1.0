package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("arc_bind_volume_item")
public class BindVolumeItem {
    @TableId(type = IdType.AUTO)
    private Long itemId;
    private Long volumeId;
    private Long archiveId;
    private Integer sortNo;
    private String primaryFlag;
    private String bindReason;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
