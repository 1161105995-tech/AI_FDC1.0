package com.smartarchive.knowledgegraph.controller;

import com.smartarchive.common.api.ApiResponse;
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
import com.smartarchive.knowledgegraph.service.ProcurementKnowledgeGraphService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledge-graph/procurement")
@RequiredArgsConstructor
public class ProcurementKnowledgeGraphController {
    private final ProcurementKnowledgeGraphService procurementKnowledgeGraphService;

    @GetMapping("/rules")
    public ApiResponse<List<MatchingRuleVersionResponse>> listRules() {
        return ApiResponse.success(procurementKnowledgeGraphService.listRules());
    }

    @PutMapping("/rules/{scenarioCode}")
    public ApiResponse<MatchingRuleVersionResponse> updateRule(@PathVariable String scenarioCode,
                                                               @Valid @RequestBody UpdateMatchingRuleCommand command) {
        return ApiResponse.success(procurementKnowledgeGraphService.updateRule(scenarioCode, command));
    }

    @PostMapping("/rebuild")
    public ApiResponse<RebuildTaskResponse> rebuild(@RequestBody RebuildTaskRequest request) {
        return ApiResponse.success(procurementKnowledgeGraphService.rebuild(request));
    }

    @GetMapping("/chains/{archiveId}")
    public ApiResponse<ProcurementChainResponse> getChainByArchiveId(@PathVariable Long archiveId) {
        return ApiResponse.success(procurementKnowledgeGraphService.getChainByArchiveId(archiveId));
    }

    @GetMapping("/contracts/{contractNo}")
    public ApiResponse<ProcurementChainResponse> getContractChain(@PathVariable String contractNo) {
        return ApiResponse.success(procurementKnowledgeGraphService.getContractChain(contractNo));
    }

    @GetMapping("/suppliers/{supplierName}/accounts")
    public ApiResponse<SupplierAccountTimelineResponse> getSupplierAccounts(@PathVariable String supplierName) {
        return ApiResponse.success(procurementKnowledgeGraphService.getSupplierAccounts(supplierName));
    }

    @GetMapping("/contracts/{contractNo}/compliance")
    public ApiResponse<ContractComplianceResponse> getContractCompliance(@PathVariable String contractNo) {
        return ApiResponse.success(procurementKnowledgeGraphService.getContractCompliance(contractNo));
    }

    @PostMapping("/ask")
    public ApiResponse<ProcurementAskResponse> ask(@Valid @RequestBody ProcurementAskRequest request) {
        return ApiResponse.success(procurementKnowledgeGraphService.ask(request));
    }

    @PostMapping("/conversations")
    public ApiResponse<ProcurementConversationResponse> createConversation(@RequestBody CreateConversationRequest request) {
        return ApiResponse.success(procurementKnowledgeGraphService.createConversation(request));
    }

    @GetMapping("/conversations")
    public ApiResponse<List<ProcurementConversationResponse>> listConversations() {
        return ApiResponse.success(procurementKnowledgeGraphService.listConversations());
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ApiResponse<List<ProcurementConversationMessageResponse>> listConversationMessages(@PathVariable Long conversationId) {
        return ApiResponse.success(procurementKnowledgeGraphService.listConversationMessages(conversationId));
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public ApiResponse<ProcurementConversationMessageResponse> sendConversationMessage(@PathVariable Long conversationId,
                                                                                       @Valid @RequestBody SendConversationMessageRequest request) {
        return ApiResponse.success(procurementKnowledgeGraphService.sendConversationMessage(conversationId, request));
    }
}
