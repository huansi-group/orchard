package com.huansi.dataagent.harness;

import io.agentscope.core.model.ModelRegistry;
import io.agentscope.core.state.AgentStateStore;
import io.agentscope.core.state.InMemoryAgentStateStore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AgentHarnessProperties.class)
public class HarnessConfig {

    @Bean
    public HarnessModelResolver harnessModelResolver() {
        return ModelRegistry::resolve;
    }

    /**
     * 演示环境使用进程内会话存储；生产环境可替换为 Redis/数据库等分布式实现。
     */
    @Bean
    public AgentStateStore harnessAgentStateStore() {
        return new InMemoryAgentStateStore();
    }
}
