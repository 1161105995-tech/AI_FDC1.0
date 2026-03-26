package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("arc_archive_ext_value")
public class ArchiveExtValue {
    @TableId(type = IdType.AUTO)
    private Long valueId;
    private Long archiveId;
    private String fieldCode;
    private String fieldNameSnapshot;
    private String fieldType;
    private String dictCategoryCode;
    private String dictItemCode;
    private String dictItemNameSnapshot;
    private String textValue;
    private String valueSource;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
