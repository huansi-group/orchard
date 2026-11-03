package com.huansi.dataagent.dashboard;

public record DashboardSummary(
        long totalAgents,
        long runningAgents,
        long pausedAgents,
        long draftAgents,
        long dataSourceTypes) {
}
