package com.smartarchive.archivemanage.service;

import com.smartarchive.archivemanage.dto.AiModelConfigResponse;
import com.smartarchive.archivemanage.dto.ArchiveAskCommand;
import com.smartarchive.archivemanage.dto.ArchiveAskResponse;
import com.smartarchive.archivemanage.dto.ArchiveAttachmentResponse;
import com.smartarchive.archivemanage.dto.ArchiveAttachmentUpdateCommand;
import com.smartarchive.archivemanage.dto.ArchiveCreateCommand;
import com.smartarchive.archivemanage.dto.ArchiveCreateOptionsResponse;
import com.smartarchive.archivemanage.dto.ArchiveCreateSessionCommand;
import com.smartarchive.archivemanage.dto.ArchiveCreateSessionResponse;
import com.smartarchive.archivemanage.dto.ArchiveDefaultResolveResponse;
import com.smartarchive.archivemanage.dto.ArchiveQueryCommand;
import com.smartarchive.archivemanage.dto.ArchiveQueryResponse;
import com.smartarchive.archivemanage.dto.ArchiveSummaryResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ArchiveManagementService {
    ArchiveCreateOptionsResponse loadCreateOptions();
    ArchiveDefaultResolveResponse resolveDefaults(String companyProjectCode, String documentTypeCode, String customRule, String archiveDestination);
    ArchiveCreateSessionResponse createSession(ArchiveCreateSessionCommand command);
    ArchiveCreateSessionResponse getSession(String sessionCode);
    ArchiveAttachmentResponse uploadAttachment(String sessionCode, String attachmentRole, String attachmentTypeCode, String remark, MultipartFile file);
    ArchiveAttachmentResponse updateAttachment(String sessionCode, Long attachmentId, ArchiveAttachmentUpdateCommand command);
    ArchiveSummaryResponse createArchive(ArchiveCreateCommand command);
    ArchiveQueryResponse queryArchives(ArchiveQueryCommand command);
    ArchiveAskResponse ask(ArchiveAskCommand command);
    List<AiModelConfigResponse> listAiModels();
}
