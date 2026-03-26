package com.smartarchive.companyproject.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("md_company_project_org_category")
public class CompanyProjectOrgCategory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String categoryCode;
    private String categoryName;
    private Integer sortOrder;
    private String enabledFlag;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
