package com.huansi.dataagent.harness;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.time.Duration;

/**
 * AgentScope Harness 配置。
 *
 * <p>默认使用 DashScope 的 qwen-plus 模型；需要设置 {@code DASHSCOPE_API_KEY} 才能实际调用模型。
 */
@ConfigurationProperties(prefix = "app.harness")
public record AgentHarnessProperties(
        String model,
        Path workspace,
        int maxIters,
        Duration timeout) {

    public AgentHarnessProperties {
        model = model == null || model.isBlank() ? "dashscope:qwen-plus" : model;
        workspace = workspace == null ? Path.of("data/harness-workspace") : workspace;
        maxIters = maxIters <= 0 ? 6 : maxIters;
        timeout = timeout == null ? Duration.ofSeconds(90) : timeout;
    }
}
