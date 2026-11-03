package com.huansi.dataagent.harness;

import io.agentscope.core.model.Model;

/**
 * 将配置中的模型标识解析为 AgentScope 的 {@link Model}。
 *
 * <p>抽成接口便于测试时注入确定性的模型实现，而不必调用外部大模型服务。
 */
@FunctionalInterface
public interface HarnessModelResolver {

    Model resolve(String modelId);
}
