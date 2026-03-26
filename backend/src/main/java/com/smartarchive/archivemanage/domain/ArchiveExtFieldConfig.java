package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("arc_ext_field_config")
public class ArchiveExtFieldConfig {
    @TableId(type = IdType.AUTO)
    private Long fieldId;
    private String fieldCode;
    private String documentTypeCode;
    private String fieldName;
    private String fieldType;
    private String dictCategoryCode;
    private String requiredFlag;
    private String enabledFlag;
    private Integer formSortOrder;
    private String queryEnabledFlag;
    private Integer querySortOrder;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
