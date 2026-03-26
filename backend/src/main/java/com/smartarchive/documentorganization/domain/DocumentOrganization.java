package com.smartarchive.documentorganization.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("md_document_organization")
public class DocumentOrganization {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String documentOrganizationCode;
    private String documentOrganizationName;
    private String description;
    private String countryCode;
    private String cityCode;
    private String enabledFlag;
    @TableLogic(value = "Y", delval = "N")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
