package com.huansi.dataagent.agent;

import com.huansi.dataagent.agent.dto.AgentRequest;
import com.huansi.dataagent.agent.dto.AgentResponse;
import com.huansi.dataagent.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final DataAgentService agentService;

    public AgentController(DataAgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping
    public ApiResponse<List<AgentResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) AgentStatus status) {
        return ApiResponse.ok(agentService.list(keyword, status));
    }

    @PostMapping
    public ApiResponse<AgentResponse> create(@Valid @RequestBody AgentRequest request,
                                             Authentication authentication) {
        return ApiResponse.ok("创建成功", agentService.create(request, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ApiResponse<AgentResponse> update(@PathVariable Long id,
                                             @Valid @RequestBody AgentRequest request) {
        return ApiResponse.ok("更新成功", agentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        agentService.delete(id);
        return ApiResponse.ok("删除成功", null);
    }
}
