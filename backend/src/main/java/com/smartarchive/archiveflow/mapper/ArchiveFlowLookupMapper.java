package com.smartarchive.archiveflow.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ArchiveFlowLookupMapper {
    @Select("""
        SELECT document_organization_code
        FROM md_document_organization
        WHERE enabled_flag = 'Y'
          AND delete_flag IN ('N', 'Y')
        ORDER BY document_organization_code
        """)
    List<String> selectEnabledDocumentOrganizationCodes();

    @Select("""
        SELECT COUNT(1)
        FROM md_document_organization
        WHERE document_organization_code = #{code}
          AND enabled_flag = 'Y'
          AND delete_flag IN ('N', 'Y')
        """)
    Integer countEnabledDocumentOrganizationCode(@Param("code") String code);
}
