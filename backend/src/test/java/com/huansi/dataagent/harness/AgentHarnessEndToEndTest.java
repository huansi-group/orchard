package com.huansi.dataagent.harness;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.TextBlock;
import io.agentscope.core.model.ChatResponse;
import io.agentscope.core.model.GenerateOptions;
import io.agentscope.core.model.Model;
import io.agentscope.core.model.ToolSchema;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 通过真实 HTTP + 真实 AgentScope Harness 执行一次对话；仅将模型替换为确定性 Stub，
 * 从而在不依赖外部大模型服务的前提下验证整条链路。
 */
@SpringBootTest(properties = {
        "app.harness.model=stub:test",
        "app.harness.workspace=target/test-harness-workspace"
})
@AutoConfigureMockMvc
@Import(AgentHarnessEndToEndTest.StubModelConfig.class)
class AgentHarnessEndToEndTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void chatRunsThroughAgentScopeHarness() throws Exception {
        String token = login("analyst", "analyst123");
        mockMvc.perform(post("/api/agents/1/chat")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"message":"平台上有哪些智能体？","sessionId":"e2e"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.agentId").value(1))
                .andExpect(jsonPath("$.data.sessionId").value("agent-1-e2e"))
                .andExpect(jsonPath("$.data.model").value("stub:test"))
                .andExpect(jsonPath("$.data.reply").value("Harness 测试回复"));
    }

    private String login(String username, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"%s","password":"%s"}
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(response);
        return json.path("data").path("token").asText();
    }

    @TestConfiguration
    static class StubModelConfig {

        @Bean
        @Primary
        HarnessModelResolver stubHarnessModelResolver() {
            return modelId -> new StubModel();
        }
    }

    private static final class StubModel implements Model {

        @Override
        public Flux<ChatResponse> stream(List<Msg> messages, List<ToolSchema> tools, GenerateOptions options) {
            return Flux.just(ChatResponse.builder()
                    .id("stub-e2e")
                    .content(List.of(TextBlock.builder().text("Harness 测试回复").build()))
                    .finishReason("stop")
                    .build());
        }

        @Override
        public String getModelName() {
            return "stub";
        }
    }
}
