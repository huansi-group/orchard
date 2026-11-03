package com.huansi.dataagent.agent.dto;

import com.huansi.dataagent.agent.AgentStatus;
import com.huansi.dataagent.agent.DataSourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AgentRequest(
        @NotBlank(message = "请输入智能体名称")
        @Size(min = 2, max = 64, message = "名称长度需为 2-64 个字符")
        String name,

        @Size(max = 500, message = "描述不能超过 500 个字符")
        String description,

        @NotNull(message = "请选择数据源类型")
        DataSourceType dataSourceType,

        @NotNull(message = "请选择状态")
        AgentStatus status) {
}
