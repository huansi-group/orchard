package com.huansi.dataagent.harness;

import com.huansi.dataagent.agent.AgentStatus;
import com.huansi.dataagent.agent.DataAgent;
import com.huansi.dataagent.agent.DataAgentRepository;
import com.huansi.dataagent.dashboard.DashboardService;
import com.huansi.dataagent.dashboard.DashboardSummary;
import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * 提供给 AgentScope Harness 的平台查询工具。
 *
 * <p>工具方法返回纯文本，便于模型直接阅读；所有工具均为只读，避免智能体修改业务数据。
 */
@Component
public class DataAgentPlatformTools {

    private final DataAgentRepository agentRepository;
    private final DashboardService dashboardService;

    public DataAgentPlatformTools(DataAgentRepository agentRepository, DashboardService dashboardService) {
        this.agentRepository = agentRepository;
        this.dashboardService = dashboardService;
    }

    @Tool(
            name = "list_data_agents",
            description = "List the data agents managed by the DataAgent platform, including name, "
                    + "data source type, status, owner and description. Use this when the user asks "
                    + "which agents exist or wants an inventory of the platform.",
            readOnly = true)
    public String listAgents(
            @ToolParam(
                    name = "status",
                    description = "Optional status filter. One of RUNNING, PAUSED or DRAFT.",
                    required = false)
            String status) {
        AgentStatus filter = null;
        if (status != null && !status.isBlank()) {
            try {
                filter = AgentStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                return "不支持的状态: " + status + "。可选值: RUNNING, PAUSED, DRAFT。";
            }
        }

        List<DataAgent> agents = agentRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
        StringBuilder result = new StringBuilder();
        int count = 0;
        for (DataAgent agent : agents) {
            if (filter != null && agent.getStatus() != filter) {
                continue;
            }
            count++;
            result.append("- #").append(agent.getId())
                    .append(" ").append(agent.getName())
                    .append(" | 数据源=").append(agent.getDataSourceType())
                    .append(" | 状态=").append(agent.getStatus())
                    .append(" | 负责人=").append(agent.getOwner());
            if (agent.getDescription() != null && !agent.getDescription().isBlank()) {
                result.append(" | 描述=").append(agent.getDescription());
            }
            result.append('\n');
        }
        if (count == 0) {
            return "当前没有符合条件的智能体。";
        }
        return "共 " + count + " 个智能体：\n" + result;
    }

    @Tool(
            name = "get_platform_summary",
            description = "Get aggregate statistics for the DataAgent platform: total agents, "
                    + "running, paused, draft counts and the number of data source types.",
            readOnly = true)
    public String getPlatformSummary() {
        DashboardSummary summary = dashboardService.summary();
        return "智能体总数=" + summary.totalAgents()
                + ", 运行中=" + summary.runningAgents()
                + ", 已暂停=" + summary.pausedAgents()
                + ", 草稿=" + summary.draftAgents()
                + ", 数据源类型数=" + summary.dataSourceTypes();
    }
}
