import { http } from './http'
import type {
  AgentPayload,
  AgentStatus,
  ApiEnvelope,
  DashboardSummary,
  DataAgent,
} from '@/types'

export async function fetchAgents(params: {
  keyword?: string
  status?: AgentStatus | ''
} = {}): Promise<DataAgent[]> {
  const response = await http.get<ApiEnvelope<DataAgent[]>>('/agents', {
    params: {
      keyword: params.keyword || undefined,
      status: params.status || undefined,
    },
  })
  return response.data.data
}

export async function createAgent(payload: AgentPayload): Promise<DataAgent> {
  const response = await http.post<ApiEnvelope<DataAgent>>('/agents', payload)
  return response.data.data
}

export async function updateAgent(id: number, payload: AgentPayload): Promise<DataAgent> {
  const response = await http.put<ApiEnvelope<DataAgent>>(`/agents/${id}`, payload)
  return response.data.data
}

export async function deleteAgent(id: number): Promise<void> {
  await http.delete<ApiEnvelope<void>>(`/agents/${id}`)
}

export async function fetchDashboardSummary(): Promise<DashboardSummary> {
  const response = await http.get<ApiEnvelope<DashboardSummary>>('/dashboard/summary')
  return response.data.data
}
