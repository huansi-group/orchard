package com.huansi.dataagent.dashboard;

import com.huansi.dataagent.agent.AgentStatus;
import com.huansi.dataagent.agent.DataAgent;
import com.huansi.dataagent.agent.DataAgentRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final DataAgentRepository agentRepository;

    public DashboardService(DataAgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public DashboardSummary summary() {
        var agents = agentRepository.findAll();
        long running = agents.stream().filter(a -> a.getStatus() == AgentStatus.RUNNING).count();
        long paused = agents.stream().filter(a -> a.getStatus() == AgentStatus.PAUSED).count();
        long draft = agents.stream().filter(a -> a.getStatus() == AgentStatus.DRAFT).count();
        long dataSourceTypes = agents.stream()
                .map(DataAgent::getDataSourceType)
                .distinct()
                .count();
        return new DashboardSummary(agents.size(), running, paused, draft, dataSourceTypes);
    }
}
