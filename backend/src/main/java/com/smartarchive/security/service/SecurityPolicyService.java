package com.smartarchive.security.service;

import com.smartarchive.security.dto.SecurityPolicyDto;
import java.util.List;

public interface SecurityPolicyService {
    List<SecurityPolicyDto> listPolicies();
}
