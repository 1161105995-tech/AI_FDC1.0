package com.smartarchive.ai.service.impl;

import com.smartarchive.ai.dto.AiCapabilityDto;
import com.smartarchive.ai.service.AiCapabilityService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AiCapabilityServiceImpl implements AiCapabilityService {
    @Override
    public List<AiCapabilityDto> listCapabilities() {
        return List.of(
            new AiCapabilityDto("OCR", "OCR与版面分析", "ENABLED", "用于扫描件、图片和 PDF 的文字识别与结构抽取"),
            new AiCapabilityDto("CLASSIFY", "自动分类", "ENABLED", "基于规则和样本数据推荐档案类型"),
            new AiCapabilityDto("METADATA", "元数据抽取", "ENABLED", "自动抽取题名、日期、责任者和文号"),
            new AiCapabilityDto("SEMANTIC_SEARCH", "语义检索", "PLANNED", "支持自然语言检索和智能问答")
        );
    }
}
