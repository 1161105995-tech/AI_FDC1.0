package com.smartarchive.knowledgegraph.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimelineEventDto {
    private String eventDate;
    private String eventType;
    private String title;
    private String description;
    private Long archiveId;
}
