package com.huansi.dataagent.harness;

import com.huansi.dataagent.agent.AgentStatus;
import com.huansi.dataagent.agent.DataAgent;
import com.huansi.dataagent.agent.DataAgentRepository;
import com.huansi.dataagent.agent.DataSourceType;
import com.huansi.dataagent.common.ApiException;
import com.huansi.dataagent.dashboard.DashboardService;
import com.huansi.dataagent.harness.dto.HarnessChatRequest;
import com.huansi.dataagent.harness.dto.HarnessChatResponse;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.TextBlock;
import io.agentscope.core.model.ChatResponse;
import io.agentscope.core.model.GenerateOptions;
import io.agentscope.core.model.Model;
import io.agentscope.core.model.ToolSchema;
import io.agentscope.core.state.InMemoryAgentStateStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AgentHarnessServiceTest {

    @TempDir
    Path workspace;

    @Test
    void runsAgentScopeHarnessWithProvidedModel() {
        DataAgent agent = mockAgent();
        DataAgentRepository repository = mock(DataAgentRepository.class);
        when(repository.findById(7L)).thenReturn(Optional.of(agent));

        AgentHarnessService service = new AgentHarnessService(
                repository,
                new DataAgentPlatformTools(repository, mock(DashboardService.class)),
                properties("stub:test"),
                modelId -> new StubModel("这是来自 Stub Model 的回复"),
                new InMemoryAgentStateStore());

        HarnessChatResponse response = service.chat(
                7L,
                "analyst",
                new HarnessChatRequest("平台上有哪些智能体？", null));

        assertThat(response.agentId()).isEqualTo(7L);
        assertThat(response.sessionId()).isEqualTo("agent-7-default");
        assertThat(response.model()).isEqualTo("stub:test");
        assertThat(response.reply()).isEqualTo("这是来自 Stub Model 的回复");
    }

    @Test
    void returnsServiceUnavailableWhenModelCannotBeResolved() {
        DataAgent agent = mockAgent();
        DataAgentRepository repository = mock(DataAgentRepository.class);
        when(repository.findById(7L)).thenReturn(Optional.of(agent));

        AgentHarnessService service = new AgentHarnessService(
                repository,
                new DataAgentPlatformTools(repository, mock(DashboardService.class)),
                properties("dashscope:qwen-plus"),
                modelId -> {
                    throw new IllegalStateException("DASHSCOPE_API_KEY is required");
                },
                new InMemoryAgentStateStore());

        assertThatThrownBy(() -> service.chat(
                7L,
                "analyst",
                new HarnessChatRequest("你好", null)))
                .isInstanceOfSatisfying(ApiException.class, ex -> {
                    assertThat(ex.getStatus().value()).isEqualTo(503);
                    assertThat(ex.getMessage()).contains("DASHSCOPE_API_KEY");
                });
    }

    @Test
    void rejectsUnknownAgent() {
        DataAgentRepository repository = mock(DataAgentRepository.class);
        when(repository.findById(99L)).thenReturn(Optional.empty());

        AgentHarnessService service = new AgentHarnessService(
                repository,
                new DataAgentPlatformTools(repository, mock(DashboardService.class)),
                properties("stub:test"),
                modelId -> new StubModel("unused"),
                new InMemoryAgentStateStore());

        assertThatThrownBy(() -> service.chat(
                99L,
                "analyst",
                new HarnessChatRequest("你好", null)))
                .isInstanceOfSatisfying(ApiException.class,
                        ex -> assertThat(ex.getStatus().value()).isEqualTo(404));
    }

    private AgentHarnessProperties properties(String model) {
        return new AgentHarnessProperties(model, workspace, 4, Duration.ofSeconds(20));
    }

    private DataAgent mockAgent() {
        DataAgent agent = mock(DataAgent.class);
        when(agent.getId()).thenReturn(7L);
        when(agent.getName()).thenReturn("销售数据分析助手");
        when(agent.getDescription()).thenReturn("分析销售趋势");
        when(agent.getDataSourceType()).thenReturn(DataSourceType.SQL);
        when(agent.getStatus()).thenReturn(AgentStatus.RUNNING);
        return agent;
    }

    private static final class StubModel implements Model {

        private final String reply;

        private StubModel(String reply) {
            this.reply = reply;
        }

        @Override
        public Flux<ChatResponse> stream(List<Msg> messages, List<ToolSchema> tools, GenerateOptions options) {
            return Flux.just(ChatResponse.builder()
                    .id("stub-response")
                    .content(List.of(TextBlock.builder().text(reply).build()))
                    .finishReason("stop")
                    .build());
        }

        @Override
        public String getModelName() {
            return "stub";
        }
    }
}
