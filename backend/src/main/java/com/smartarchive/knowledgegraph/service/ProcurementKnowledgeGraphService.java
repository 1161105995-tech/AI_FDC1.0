package com.smartarchive.knowledgegraph.service;

import com.smartarchive.knowledgegraph.dto.ContractComplianceResponse;
import com.smartarchive.knowledgegraph.dto.CreateConversationRequest;
import com.smartarchive.knowledgegraph.dto.MatchingRuleVersionResponse;
import com.smartarchive.knowledgegraph.dto.ProcurementAskRequest;
import com.smartarchive.knowledgegraph.dto.ProcurementAskResponse;
import com.smartarchive.knowledgegraph.dto.ProcurementChainResponse;
import com.smartarchive.knowledgegraph.dto.ProcurementConversationMessageResponse;
import com.smartarchive.knowledgegraph.dto.ProcurementConversationResponse;
import com.smartarchive.knowledgegraph.dto.RebuildTaskRequest;
import com.smartarchive.knowledgegraph.dto.RebuildTaskResponse;
import com.smartarchive.knowledgegraph.dto.SendConversationMessageRequest;
import com.smartarchive.knowledgegraph.dto.SupplierAccountTimelineResponse;
import com.smartarchive.knowledgegraph.dto.UpdateMatchingRuleCommand;
import java.util.List;

public interface ProcurementKnowledgeGraphService {
    List<MatchingRuleVersionResponse> listRules();
    MatchingRuleVersionResponse updateRule(String scenarioCode, UpdateMatchingRuleCommand command);
    RebuildTaskResponse rebuild(RebuildTaskRequest request);
    ProcurementChainResponse getChainByArchiveId(Long archiveId);
    ProcurementChainResponse getContractChain(String contractNo);
    SupplierAccountTimelineResponse getSupplierAccounts(String supplierName);
    ContractComplianceResponse getContractCompliance(String contractNo);
    ProcurementAskResponse ask(ProcurementAskRequest request);
    ProcurementConversationResponse createConversation(CreateConversationRequest request);
    List<ProcurementConversationResponse> listConversations();
    List<ProcurementConversationMessageResponse> listConversationMessages(Long conversationId);
    ProcurementConversationMessageResponse sendConversationMessage(Long conversationId, SendConversationMessageRequest request);
}
