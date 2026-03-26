package com.smartarchive.rule.service;

import com.smartarchive.rule.dto.RuleDefinitionDto;
import java.util.List;

public interface RuleService {
    List<RuleDefinitionDto> listRules();
}
