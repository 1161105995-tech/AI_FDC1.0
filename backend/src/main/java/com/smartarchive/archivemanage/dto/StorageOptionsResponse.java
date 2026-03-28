package com.smartarchive.archivemanage.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StorageOptionsResponse {
    private List<LabelValueOption> sourceTypes;
    private List<LabelValueOption> warehouses;
    private List<LabelValueOption> locations;
}
