package com.smartarchive.archivemanage.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("arc_archive")
public class ArchiveRecord {
    @TableId(type = IdType.AUTO)
    private Long archiveId;
    private String archiveCode;
    private String archiveFilingCode;
    private String createMode;
    private String archiveStatus;
    private String documentTypeCode;
    private String companyProjectCode;
    private String beginPeriod;
    private String endPeriod;
    private String businessCode;
    private String documentName;
    private String dutyPerson;
    private String dutyDepartment;
    private LocalDate documentDate;
    private String securityLevelCode;
    private String sourceSystem;
    private String archiveDestination;
    private String originPlace;
    private String carrierTypeCode;
    @TableField(exist = false)
    private String bindVolumeCode;
    @TableField(exist = false)
    private String currentWarehouseCode;
    @TableField(exist = false)
    private String currentLocationCode;
    private String remark;
    private String aiArchiveSummary;
    private BigDecimal aiParseConfidence;
    private String documentOrganizationCode;
    private Integer retentionPeriodYears;
    private String archiveTypeCode;
    private String countryCode;
    private String parseStatus;
    private String vectorStatus;
    private String qaIndexStatus;
    private String fourNatureCheckStatus;
    private Long fourNatureReportId;
    private Long sessionId;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
