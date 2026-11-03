package com.huansi.dataagent.harness;

import com.huansi.dataagent.agent.DataAgent;
import com.huansi.dataagent.agent.DataAgentRepository;
import com.huansi.dataagent.common.ApiException;
import com.huansi.dataagent.harness.dto.HarnessChatRequest;
import com.huansi.dataagent.harness.dto.HarnessChatResponse;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.UserMessage;
import io.agentscope.core.model.Model;
import io.agentscope.core.state.AgentStateStore;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.harness.agent.HarnessAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;

/**
 * 基于 AgentScope Java 2.0.x Harness 的智能体运行时。
 *
 * <p>每次请求构建一个 HarnessAgent，使用稳定的 agentId + userId + sessionId 恢复会话，
 * 请求结束后关闭实例；模型与会话存储由 Spring Bean 管理。
 */
@Service
public class AgentHarnessService {

    private static final Logger log = LoggerFactory.getLogger(AgentHarnessService.class);

    private final DataAgentRepository agentRepository;
    private final DataAgentPlatformTools platformTools;
    private final AgentHarnessProperties properties;
    private final HarnessModelResolver modelResolver;
    private final AgentStateStore stateStore;

    public AgentHarnessService(DataAgentRepository agentRepository,
                               DataAgentPlatformTools platformTools,
                               AgentHarnessProperties properties,
                               HarnessModelResolver modelResolver,
                               AgentStateStore stateStore) {
        this.agentRepository = agentRepository;
        this.platformTools = platformTools;
        this.properties = properties;
        this.modelResolver = modelResolver;
        this.stateStore = stateStore;
    }

    public HarnessChatResponse chat(Long agentId, String username, HarnessChatRequest request) {
        DataAgent dataAgent = agentRepository.findById(agentId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "智能体不存在"));

        Model model = resolveModel();
        String sessionId = resolveSessionId(agentId, request.sessionId());
        ensureWorkspace();

        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(platformTools);

        HarnessAgent.Builder builder = HarnessAgent.builder()
                .name("dataagent-" + dataAgent.getId())
                .agentId("dataagent-" + dataAgent.getId())
                .description(dataAgent.getName())
                .sysPrompt(buildSystemPrompt(dataAgent))
                .model(model)
                .workspace(properties.workspace())
                .stateStore(stateStore)
                .toolkit(toolkit)
                .maxIters(properties.maxIters())
                .disableFilesystemTools()
                .disableShellTool()
                .disableWebTools()
                .disableSubagents();

        RuntimeContext context = RuntimeContext.builder()
                .userId(username)
                .sessionId(sessionId)
                .build();

        try (HarnessAgent harnessAgent = builder.build()) {
            Msg reply = harnessAgent
                    .call(new UserMessage(request.message().trim()), context)
                    .block(properties.timeout());
            if (reply == null) {
                throw new ApiException(HttpStatus.BAD_GATEWAY, "Agent 未返回结果，请稍后重试");
            }
            String text = reply.getTextContent();
            if (text == null || text.isBlank()) {
                throw new ApiException(HttpStatus.BAD_GATEWAY, "Agent 返回了空结果，请稍后重试");
            }
            return new HarnessChatResponse(dataAgent.getId(), sessionId, properties.model(), text);
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("AgentScope harness call failed for agent {}", agentId, ex);
            if (isTimeout(ex)) {
                throw new ApiException(HttpStatus.GATEWAY_TIMEOUT, "Agent 运行超时，请稍后重试");
            }
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Agent 运行失败，请检查模型配置或稍后重试");
        }
    }

    private Model resolveModel() {
        try {
            return modelResolver.resolve(properties.model());
        } catch (RuntimeException ex) {
            log.warn("AgentScope model '{}' is not available: {}", properties.model(), ex.getMessage());
            throw new ApiException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Agent 模型未就绪，请配置 DASHSCOPE_API_KEY（当前模型：" + properties.model() + "）");
        }
    }

    private void ensureWorkspace() {
        try {
            Files.createDirectories(properties.workspace());
        } catch (IOException ex) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Agent 工作目录创建失败");
        }
    }

    private String resolveSessionId(Long agentId, String requestedSessionId) {
        String suffix = requestedSessionId == null || requestedSessionId.isBlank()
                ? "default"
                : requestedSessionId.trim();
        return "agent-" + agentId + "-" + suffix;
    }

    private String buildSystemPrompt(DataAgent agent) {
        String description = agent.getDescription() == null || agent.getDescription().isBlank()
                ? "负责该数据智能体相关的数据问答与分析。"
                : agent.getDescription();
        return """
                你是 DataAgent 平台上的数据智能体「%s」。
                你的职责：%s
                数据源类型：%s；当前状态：%s。
                你可以调用 list_data_agents 和 get_platform_summary 工具查询平台上的智能体与统计数据。
                回答要求：使用简体中文，先给结论再给必要的依据；只基于已知信息与工具结果回答，不要编造数据。
                """.formatted(
                agent.getName(),
                description,
                agent.getDataSourceType(),
                agent.getStatus());
    }

    private boolean isTimeout(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof java.util.concurrent.TimeoutException) {
                return true;
            }
            String message = current.getMessage();
            if (message != null && message.toLowerCase().contains("timeout")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
