package com.smartarchive.documenttype.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("doc_document_type")
public class DocumentType {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String typeCode;
    private String typeName;
    private String description;
    private String enabledFlag;
    private String parentCode;
    private Integer levelNum;
    private String ancestorPath;
    private Integer sortOrder;
    @TableLogic(value = "N", delval = "Y")
    private String deleteFlag;
    private Long createdBy;
    private LocalDateTime creationDate;
    private Long lastUpdatedBy;
    private LocalDateTime lastUpdateDate;
}
