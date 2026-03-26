package com.smartarchive.rule.service.impl;

import com.smartarchive.rule.dto.RuleDefinitionDto;
import com.smartarchive.rule.service.RuleService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RuleServiceImpl implements RuleService {
    @Override
    public List<RuleDefinitionDto> listRules() {
        return List.of(
            new RuleDefinitionDto("RETENTION_CONTRACT", "合同保管期限规则", "RETENTION", "archiveType == '合同档案' -> 30年"),
            new RuleDefinitionDto("SECURITY_HR", "人事档案密级规则", "SECURITY", "classificationCode startsWith 'HR' -> SECRET"),
            new RuleDefinitionDto("WAREHOUSE_CAPACITY", "库位容量预警规则", "WAREHOUSE", "occupiedCount / capacity >= 0.9")
        );
    }
}
