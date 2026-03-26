package com.smartarchive.archive.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("arc_borrow_record")
public class BorrowRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String borrowCode;
    private String archiveCode;
    private String archiveTitle;
    private String borrower;
    private String borrowType;
    private String approvalStatus;
    private String borrowStatus;
    private LocalDate expectedReturnDate;
    private LocalDateTime borrowedAt;
}