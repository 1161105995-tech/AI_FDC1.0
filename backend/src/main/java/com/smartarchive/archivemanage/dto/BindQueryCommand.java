package com.smartarchive.archivemanage.dto;

import lombok.Data;

@Data
public class BindQueryCommand {
    private String bindMode;
    private String bindStatus;
    private String keyword;
}
