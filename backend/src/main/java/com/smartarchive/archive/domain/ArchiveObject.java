package com.smartarchive.archive.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("arc_archive_object")
public class ArchiveObject {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String archiveCode;
    private String title;
    private String archiveType;
    private String classificationCode;
    private String securityLevel;
    private String retentionPeriod;
    private String organizationName;
    private String fondsName;
    private String carrierType;
    private String physicalStatus;
    private String digitalStatus;
    private String currentWorkflowStage;
    private String currentWarehouseCode;
    private String currentLocationCode;
    private String responsiblePerson;
    private LocalDate formedDate;
    private Integer fileCount;
    private Integer pageCount;
    private Boolean aiClassified;
    private Boolean aiMetadataExtracted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
