package com.huansi.dataagent.harness.dto;

public record HarnessChatResponse(
        Long agentId,
        String sessionId,
        String model,
        String reply) {
}
