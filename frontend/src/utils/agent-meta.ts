import type { AgentStatus, DataSourceType } from '@/types'

type TagType = 'primary' | 'success' | 'warning' | 'danger' | 'info'

export const statusMeta: Record<AgentStatus, { label: string; type: TagType }> = {
  RUNNING: { label: '运行中', type: 'success' },
  PAUSED: { label: '已暂停', type: 'warning' },
  DRAFT: { label: '草稿', type: 'info' },
}

export const sourceMeta: Record<DataSourceType, { label: string; short: string; type: TagType }> = {
  SQL: { label: 'SQL 数据库', short: 'SQL', type: 'primary' },
  API: { label: 'API 接口', short: 'API', type: 'success' },
  FILE: { label: '文件数据集', short: 'FILE', type: 'warning' },
}

export const allSourceTypes: DataSourceType[] = ['SQL', 'API', 'FILE']
