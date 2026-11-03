export interface ApiEnvelope<T> {
  success: boolean
  message: string
  data: T
}

export type Role = 'ADMIN' | 'USER'

export interface UserProfile {
  id: number
  username: string
  displayName: string
  email: string
  role: Role
}

export interface LoginPayload {
  username: string
  password: string
}

export interface RegisterPayload {
  username: string
  email: string
  displayName: string
  password: string
}

export interface AuthResponse {
  token: string
  tokenType: string
  expiresIn: number
  user: UserProfile
}

export type DataSourceType = 'SQL' | 'API' | 'FILE'

export type AgentStatus = 'RUNNING' | 'PAUSED' | 'DRAFT'

export interface DataAgent {
  id: number
  name: string
  description: string | null
  dataSourceType: DataSourceType
  status: AgentStatus
  owner: string
  createdAt: string
  updatedAt: string
}

export interface AgentPayload {
  name: string
  description: string
  dataSourceType: DataSourceType
  status: AgentStatus
}

export interface DashboardSummary {
  totalAgents: number
  runningAgents: number
  pausedAgents: number
  draftAgents: number
  dataSourceTypes: number
}
