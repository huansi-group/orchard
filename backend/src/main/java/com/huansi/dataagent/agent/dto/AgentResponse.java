package com.huansi.dataagent.agent.dto;

import com.huansi.dataagent.agent.AgentStatus;
import com.huansi.dataagent.agent.DataAgent;
import com.huansi.dataagent.agent.DataSourceType;

import java.time.LocalDateTime;

public record AgentResponse(
        Long id,
        String name,
        String description,
        DataSourceType dataSourceType,
        AgentStatus status,
        String owner,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static AgentResponse from(DataAgent agent) {
        return new AgentResponse(
                agent.getId(),
                agent.getName(),
                agent.getDescription(),
                agent.getDataSourceType(),
                agent.getStatus(),
                agent.getOwner(),
                agent.getCreatedAt(),
                agent.getUpdatedAt());
    }
}
