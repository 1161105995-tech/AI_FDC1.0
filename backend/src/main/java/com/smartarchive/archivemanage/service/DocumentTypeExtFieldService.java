package com.smartarchive.archivemanage.service;

import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldCreateCommand;
import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldResponse;
import com.smartarchive.archivemanage.dto.DocumentTypeExtFieldUpdateCommand;
import java.util.List;

public interface DocumentTypeExtFieldService {
    List<DocumentTypeExtFieldResponse> listDirect(String documentTypeCode);
    List<DocumentTypeExtFieldResponse> listEffective(String documentTypeCode);
    DocumentTypeExtFieldResponse create(String documentTypeCode, DocumentTypeExtFieldCreateCommand command);
    DocumentTypeExtFieldResponse update(String documentTypeCode, String fieldCode, DocumentTypeExtFieldUpdateCommand command);
    void delete(String documentTypeCode, String fieldCode);
}
