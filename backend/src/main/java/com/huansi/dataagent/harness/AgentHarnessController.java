package com.huansi.dataagent.harness;

import com.huansi.dataagent.common.ApiResponse;
import com.huansi.dataagent.harness.dto.HarnessChatRequest;
import com.huansi.dataagent.harness.dto.HarnessChatResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agents")
public class AgentHarnessController {

    private final AgentHarnessService harnessService;

    public AgentHarnessController(AgentHarnessService harnessService) {
        this.harnessService = harnessService;
    }

    @PostMapping("/{agentId}/chat")
    public ApiResponse<HarnessChatResponse> chat(@PathVariable Long agentId,
                                                 @Valid @RequestBody HarnessChatRequest request,
                                                 Authentication authentication) {
        return ApiResponse.ok(
                "Agent 运行完成",
                harnessService.chat(agentId, authentication.getName(), request));
    }
}
