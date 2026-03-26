package com.smartarchive.archivemanage.service.support;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ArchiveTextChunkService {
    public List<String> chunk(String text) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return chunks;
        }
        String normalized = text.replace("\r", "").trim();
        int step = 500;
        int overlap = 80;
        for (int start = 0; start < normalized.length(); start += (step - overlap)) {
            int end = Math.min(start + step, normalized.length());
            chunks.add(normalized.substring(start, end));
            if (end == normalized.length()) {
                break;
            }
        }
        return chunks;
    }
}
