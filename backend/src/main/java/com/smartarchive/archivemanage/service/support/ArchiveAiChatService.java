package com.smartarchive.archivemanage.service.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartarchive.archivemanage.domain.AiModelConfig;
import com.smartarchive.archivemanage.dto.ArchiveSummaryResponse;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ArchiveAiChatService {
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public ArchiveAiChatService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    public String answer(AiModelConfig chatModel, String question, List<ArchiveSummaryResponse> references, List<String> evidenceSnippets) {
        if (chatModel == null || !StringUtils.hasText(chatModel.getApiUrl()) || chatModel.getApiUrl().startsWith("local://")) {
            return buildLocalAnswer(question, references, evidenceSnippets);
        }

        try {
            String systemPrompt = StringUtils.hasText(chatModel.getPromptTemplate())
                ? chatModel.getPromptTemplate()
                : "你是档案系统AI搜索助手。请基于提供的检索证据回答问题，只能根据证据作答；证据不足时要明确说明，并提醒用户查看原文。输出使用简体中文，结构清晰，避免编造。";

            String userPrompt = """
                用户问题：
                %s

                命中文档：
                %s

                命中依据片段：
                %s

                请输出：
                1. 结论摘要
                2. 关键要点
                3. 风险或注意事项
                """.formatted(
                question,
                buildReferenceContext(references),
                String.join("\n", evidenceSnippets)
            );

            JsonNode payload = objectMapper.createObjectNode()
                .put("model", chatModel.getModelIdentifier())
                .put("temperature", 0.2)
                .set("messages", objectMapper.createArrayNode()
                    .add(objectMapper.createObjectNode().put("role", "system").put("content", systemPrompt))
                    .add(objectMapper.createObjectNode().put("role", "user").put("content", userPrompt)));

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(chatModel.getApiUrl()))
                .timeout(Duration.ofSeconds(Math.max(10, Objects.requireNonNullElse(chatModel.getTimeoutSeconds(), 30))))
                .header("Content-Type", "application/json");

            if (StringUtils.hasText(chatModel.getApiKey()) && !"N/A".equalsIgnoreCase(chatModel.getApiKey())) {
                requestBuilder.header("Authorization", "Bearer " + chatModel.getApiKey());
            }

            HttpResponse<String> response = httpClient.send(
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload))).build(),
                HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
                if (contentNode.isTextual() && StringUtils.hasText(contentNode.asText())) {
                    return contentNode.asText().trim();
                }
            }
        } catch (Exception ignored) {
        }

        return buildLocalAnswer(question, references, evidenceSnippets);
    }

    private String buildReferenceContext(List<ArchiveSummaryResponse> references) {
        return references.stream()
            .map(item -> "- " + item.getDocumentName() + "（类型：" + Objects.toString(item.getDocumentTypeName(), item.getArchiveTypeCode()) + "）")
            .collect(Collectors.joining("\n"));
    }

    private String buildLocalAnswer(String question, List<ArchiveSummaryResponse> references, List<String> evidenceSnippets) {
        if (references.isEmpty()) {
            return "当前未检索到足够相关的档案内容，建议换一种问法、缩小问题范围，或直接查看文档搜索结果。";
        }
        String referenceSummary = references.stream()
            .limit(3)
            .map(item -> "《" + item.getDocumentName() + "》")
            .collect(Collectors.joining("、"));
        String evidence = evidenceSnippets.stream().filter(StringUtils::hasText).findFirst().orElse("建议继续查看右侧原文依据。");
        return "围绕“" + question + "”，系统结合 " + referenceSummary + " 进行了整理。核心结论是：请优先依据命中文档中的现行规定执行；如果涉及版本差异、权限限制或特殊流程，请以原文制度条款和正式审批要求为准。补充依据：" + evidence;
    }
}
