<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { CircleCheck, Cpu, DataLine, Plus, Right, VideoPause } from '@element-plus/icons-vue'
import { fetchAgents, fetchDashboardSummary } from '@/api/agents'
import { getErrorMessage } from '@/api/http'
import { useAuthStore } from '@/stores/auth'
import { allSourceTypes, sourceMeta, statusMeta } from '@/utils/agent-meta'
import { formatDateTime } from '@/utils/format'
import type { AgentStatus, DashboardSummary, DataAgent, DataSourceType } from '@/types'

const router = useRouter()
const auth = useAuthStore()

const summary = ref<DashboardSummary | null>(null)
const agents = ref<DataAgent[]>([])
const loading = ref(true)
const error = ref('')

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) {
    return '夜深了'
  }
  if (hour < 12) {
    return '早上好'
  }
  if (hour < 18) {
    return '下午好'
  }
  return '晚上好'
})

const stats = computed(() => [
  {
    label: '智能体总数',
    value: summary.value?.totalAgents ?? 0,
    hint: '已纳入平台的智能体',
    icon: Cpu,
    tone: 'indigo',
  },
  {
    label: '运行中',
    value: summary.value?.runningAgents ?? 0,
    hint: '正在提供分析服务',
    icon: CircleCheck,
    tone: 'green',
  },
  {
    label: '已暂停',
    value: summary.value?.pausedAgents ?? 0,
    hint: '等待恢复运行',
    icon: VideoPause,
    tone: 'amber',
  },
  {
    label: '草稿',
    value: summary.value?.draftAgents ?? 0,
    hint: '仍在配置中',
    icon: DataLine,
    tone: 'violet',
  },
])

const recentAgents = computed(() => agents.value.slice(0, 5))

const sourceColors: Record<DataSourceType, string> = {
  SQL: '#6366f1',
  API: '#10b981',
  FILE: '#f59e0b',
}

const sourceStats = computed(() => {
  const total = agents.value.length
  return allSourceTypes.map((type) => {
    const count = agents.value.filter((agent) => agent.dataSourceType === type).length
    return {
      type,
      label: sourceMeta[type].label,
      count,
      percent: total === 0 ? 0 : Math.round((count / total) * 100),
      color: sourceColors[type],
    }
  })
})

const totalAgents = computed(() => summary.value?.totalAgents ?? 0)
const runningAgents = computed(() => summary.value?.runningAgents ?? 0)

async function load() {
  loading.value = true
  error.value = ''
  try {
    const [summaryData, agentList] = await Promise.all([
      fetchDashboardSummary(),
      fetchAgents(),
    ])
    summary.value = summaryData
    agents.value = agentList
  } catch (loadError) {
    error.value = getErrorMessage(loadError)
  } finally {
    loading.value = false
  }
}

function createAgent() {
  void router.push({ path: '/agents', query: { create: '1' } })
}

onMounted(load)
</script>

<template>
  <div v-loading="loading" class="dashboard">
    <section class="hero">
      <div class="hero-copy">
        <span class="hero-kicker">工作台概览</span>
        <h2>{{ greeting }}，{{ auth.displayName }}</h2>
        <p>
          今天也从数据中寻找确定性的答案。当前共有
          <strong>{{ totalAgents }}</strong> 个智能体，其中
          <strong>{{ runningAgents }}</strong> 个正在运行。
        </p>
      </div>
      <div class="hero-actions">
        <el-button v-if="auth.isAdmin" type="primary" :icon="Plus" @click="createAgent">
          新建智能体
        </el-button>
        <el-button :icon="Right" @click="router.push('/agents')">查看全部</el-button>
      </div>
    </section>

    <el-alert
      v-if="error"
      class="load-error"
      :title="error"
      type="error"
      show-icon
      :closable="false"
    >
      <template #default>
        <el-button link type="primary" @click="load">重新加载</el-button>
      </template>
    </el-alert>

    <section class="stat-grid">
      <article v-for="stat in stats" :key="stat.label" class="stat-card">
        <span class="stat-icon" :class="`stat-icon--${stat.tone}`">
          <el-icon><component :is="stat.icon" /></el-icon>
        </span>
        <div>
          <p class="stat-label">{{ stat.label }}</p>
          <p class="stat-value">{{ stat.value }}</p>
          <p class="stat-hint">{{ stat.hint }}</p>
        </div>
      </article>
    </section>

    <div class="content-grid">
      <section class="panel recent-panel">
        <header class="panel-head">
          <div>
            <h3>最近更新</h3>
            <p>按更新时间排序的智能体</p>
          </div>
          <router-link to="/agents" class="panel-link">查看全部 →</router-link>
        </header>

        <el-table :data="recentAgents" class="recent-table">
          <el-table-column label="名称" min-width="220">
            <template #default="{ row }">
              <div class="agent-cell">
                <span class="agent-avatar">{{ row.name.slice(0, 1) }}</span>
                <div class="agent-name">
                  <strong>{{ row.name }}</strong>
                  <small>{{ row.description || '暂无描述' }}</small>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="数据源" width="110">
            <template #default="{ row }">
              <el-tag :type="sourceMeta[row.dataSourceType as DataSourceType].type" effect="plain">
                {{ sourceMeta[row.dataSourceType as DataSourceType].short }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="statusMeta[row.status as AgentStatus].type" effect="light" round>
                {{ statusMeta[row.status as AgentStatus].label }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="更新时间" width="130">
            <template #default="{ row }">
              <span class="muted">{{ formatDateTime(row.updatedAt) }}</span>
            </template>
          </el-table-column>
          <template #empty>
            <el-empty description="暂无智能体" :image-size="72" />
          </template>
        </el-table>
      </section>

      <div class="side-column">
        <section class="panel source-panel">
          <header class="panel-head">
            <div>
              <h3>数据源分布</h3>
              <p>按接入方式统计</p>
            </div>
            <span class="count-badge">{{ summary?.dataSourceTypes ?? 0 }} 类</span>
          </header>
          <div class="source-list">
            <div v-for="source in sourceStats" :key="source.type" class="source-row">
              <div class="source-meta">
                <span class="source-name">
                  <i :style="{ background: source.color }" />
                  {{ source.label }}
                </span>
                <strong>{{ source.count }} 个</strong>
              </div>
              <el-progress
                :percentage="source.percent"
                :show-text="false"
                :stroke-width="6"
                :color="source.color"
              />
            </div>
          </div>
        </section>

        <section class="panel info-panel">
          <header class="panel-head">
            <div>
              <h3>平台信息</h3>
              <p>当前运行环境</p>
            </div>
          </header>
          <ul class="info-list">
            <li><span>认证方式</span><strong>JWT（Bearer）</strong></li>
            <li><span>登录有效期</span><strong>24 小时</strong></li>
            <li><span>数据存储</span><strong>H2 内存数据库</strong></li>
            <li><span>接口前缀</span><strong>/api</strong></li>
            <li><span>前端版本</span><strong>v0.1.0</strong></li>
          </ul>
        </section>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  min-height: 320px;
}

.hero {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 30px 34px;
  overflow: hidden;
  background:
    radial-gradient(90% 140% at 100% 0%, rgba(99, 102, 241, 0.12), transparent 55%),
    linear-gradient(120deg, #ffffff 0%, #f5f6ff 100%);
  border: 1px solid #e4e7f5;
  border-radius: 18px;
}

.hero::after {
  position: absolute;
  top: -70px;
  right: 120px;
  width: 190px;
  height: 190px;
  content: '';
  border: 1px solid rgba(99, 102, 241, 0.14);
  border-radius: 50%;
}

.hero-copy {
  position: relative;
  z-index: 1;
}

.hero-kicker {
  display: inline-block;
  padding: 3px 10px;
  margin-bottom: 12px;
  font-size: 11px;
  font-weight: 600;
  color: #4338ca;
  letter-spacing: 0.1em;
  background: #eef2ff;
  border-radius: 999px;
}

.hero h2 {
  font-size: 26px;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.hero p {
  max-width: 620px;
  margin-top: 8px;
  font-size: 14px;
  color: var(--da-muted);
}

.hero p strong {
  font-weight: 600;
  color: #4338ca;
}

.hero-actions {
  position: relative;
  z-index: 1;
  display: flex;
  flex-shrink: 0;
  gap: 10px;
}

.load-error {
  margin-top: 18px;
  border-radius: 12px;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-top: 20px;
}

.stat-card {
  display: flex;
  gap: 14px;
  align-items: flex-start;
  padding: 20px;
  background: #fff;
  border: 1px solid var(--da-border);
  border-radius: 16px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.stat-card:hover {
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.07);
  transform: translateY(-2px);
}

.stat-icon {
  display: grid;
  flex-shrink: 0;
  place-items: center;
  width: 42px;
  height: 42px;
  font-size: 19px;
  border-radius: 12px;
}

.stat-icon--indigo {
  color: #4338ca;
  background: #eef2ff;
}

.stat-icon--green {
  color: #047857;
  background: #ecfdf5;
}

.stat-icon--amber {
  color: #b45309;
  background: #fffbeb;
}

.stat-icon--violet {
  color: #6d28d9;
  background: #f5f3ff;
}

.stat-label {
  font-size: 12px;
  color: var(--da-muted);
}

.stat-value {
  margin: 2px 0;
  font-size: 26px;
  font-weight: 700;
  line-height: 1.2;
  letter-spacing: -0.02em;
}

.stat-hint {
  font-size: 11px;
  color: #94a3b8;
}

.content-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.65fr) minmax(280px, 1fr);
  gap: 16px;
  margin-top: 20px;
}

.panel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  padding: 20px 22px 16px;
}

.panel-head h3 {
  font-size: 15px;
  font-weight: 600;
}

.panel-head p {
  margin-top: 2px;
  font-size: 12px;
  color: var(--da-muted);
}

.panel-link {
  font-size: 12px;
  font-weight: 500;
  color: var(--da-primary);
  white-space: nowrap;
}

.recent-table {
  width: 100%;
  border-top: 1px solid #f1f5f9;
}

.agent-cell {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.agent-avatar {
  display: grid;
  flex-shrink: 0;
  place-items: center;
  width: 34px;
  height: 34px;
  font-size: 13px;
  font-weight: 600;
  color: #4338ca;
  background: #eef2ff;
  border-radius: 10px;
}

.agent-name {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.agent-name strong {
  overflow: hidden;
  font-size: 13px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.agent-name small {
  overflow: hidden;
  max-width: 300px;
  font-size: 11px;
  color: var(--da-muted);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.side-column {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.count-badge {
  padding: 3px 9px;
  font-size: 11px;
  font-weight: 500;
  color: #4338ca;
  background: #eef2ff;
  border-radius: 999px;
}

.source-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
  padding: 4px 22px 22px;
}

.source-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 12px;
}

.source-name {
  display: inline-flex;
  gap: 8px;
  align-items: center;
  color: #475569;
}

.source-name i {
  width: 8px;
  height: 8px;
  border-radius: 3px;
}

.source-meta strong {
  font-weight: 600;
  color: var(--da-ink);
}

.info-list {
  padding: 0 22px 18px;
  margin: 0;
  list-style: none;
}

.info-list li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 9px 0;
  font-size: 12px;
  border-bottom: 1px dashed #eef2f7;
}

.info-list li:last-child {
  border-bottom: none;
}

.info-list span {
  color: var(--da-muted);
}

.info-list strong {
  font-weight: 500;
}

@media (max-width: 1180px) {
  .stat-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .content-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .hero {
    flex-direction: column;
    align-items: flex-start;
    padding: 24px;
  }

  .hero h2 {
    font-size: 22px;
  }

  .hero-actions {
    width: 100%;
  }

  .hero-actions .el-button {
    flex: 1;
  }

  .stat-grid {
    grid-template-columns: 1fr;
  }
}
</style>
