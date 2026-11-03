<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import { createAgent, deleteAgent, fetchAgents, updateAgent } from '@/api/agents'
import { getErrorMessage } from '@/api/http'
import { useAuthStore } from '@/stores/auth'
import { allSourceTypes, sourceMeta, statusMeta } from '@/utils/agent-meta'
import { formatDateTime } from '@/utils/format'
import type { AgentPayload, AgentStatus, DataAgent, DataSourceType } from '@/types'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const agents = ref<DataAgent[]>([])
const loading = ref(false)
const keyword = ref('')
const statusFilter = ref<AgentStatus | ''>('')

const dialogVisible = ref(false)
const saving = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const form = reactive<AgentPayload>({
  name: '',
  description: '',
  dataSourceType: 'SQL',
  status: 'DRAFT',
})

const rules: FormRules = {
  name: [
    { required: true, message: '请输入智能体名称', trigger: 'blur' },
    { min: 2, max: 64, message: '名称长度需为 2-64 个字符', trigger: 'blur' },
  ],
  dataSourceType: [{ required: true, message: '请选择数据源类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
}

const dialogTitle = computed(() => (editingId.value === null ? '新建智能体' : '编辑智能体'))
const resultText = computed(() => `共 ${agents.value.length} 个智能体`)

async function load() {
  loading.value = true
  try {
    agents.value = await fetchAgents({
      keyword: keyword.value,
      status: statusFilter.value,
    })
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  } finally {
    loading.value = false
  }
}

function resetForm() {
  Object.assign(form, {
    name: '',
    description: '',
    dataSourceType: 'SQL',
    status: 'DRAFT',
  } satisfies AgentPayload)
}

async function openCreate() {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
  await nextTick()
  formRef.value?.clearValidate()
}

async function openEdit(agent: DataAgent) {
  editingId.value = agent.id
  Object.assign(form, {
    name: agent.name,
    description: agent.description ?? '',
    dataSourceType: agent.dataSourceType,
    status: agent.status,
  } satisfies AgentPayload)
  dialogVisible.value = true
  await nextTick()
  formRef.value?.clearValidate()
}

async function submit() {
  if (!formRef.value) {
    return
  }
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }
  saving.value = true
  try {
    const payload: AgentPayload = {
      name: form.name.trim(),
      description: form.description.trim(),
      dataSourceType: form.dataSourceType,
      status: form.status,
    }
    if (editingId.value === null) {
      await createAgent(payload)
      ElMessage.success('智能体已创建')
    } else {
      await updateAgent(editingId.value, payload)
      ElMessage.success('智能体已更新')
    }
    dialogVisible.value = false
    await load()
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  } finally {
    saving.value = false
  }
}

async function removeAgent(agent: DataAgent) {
  try {
    await ElMessageBox.confirm(
      `确定删除「${agent.name}」吗？删除后不可恢复。`,
      '删除智能体',
      {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning',
      },
    )
  } catch {
    return
  }
  try {
    await deleteAgent(agent.id)
    ElMessage.success('智能体已删除')
    await load()
  } catch (error) {
    ElMessage.error(getErrorMessage(error))
  }
}

onMounted(async () => {
  await load()
  if (route.query.create === '1' && auth.isAdmin) {
    await openCreate()
  }
  if (route.query.create) {
    await router.replace({ path: '/agents' })
  }
})
</script>

<template>
  <div class="agents-page">
    <div class="page-head">
      <div>
        <h2>智能体</h2>
        <p>管理数据智能体的数据源、运行状态与生命周期。</p>
      </div>
      <el-button v-if="auth.isAdmin" type="primary" :icon="Plus" @click="openCreate">
        新建智能体
      </el-button>
    </div>

    <section class="panel agents-panel">
      <div class="toolbar">
        <el-input
          v-model="keyword"
          class="search-input"
          placeholder="搜索名称或描述"
          :prefix-icon="Search"
          clearable
          @keyup.enter="load"
          @clear="load"
        />
        <el-select
          v-model="statusFilter"
          class="status-select"
          placeholder="全部状态"
          clearable
          @change="load"
        >
          <el-option label="运行中" value="RUNNING" />
          <el-option label="已暂停" value="PAUSED" />
          <el-option label="草稿" value="DRAFT" />
        </el-select>
        <el-button :icon="Refresh" @click="load">刷新</el-button>
        <span class="result-count">{{ resultText }}</span>
      </div>

      <el-table v-loading="loading" :data="agents" class="agents-table">
        <el-table-column label="名称" min-width="260">
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
        <el-table-column label="数据源" width="130">
          <template #default="{ row }">
            <el-tag :type="sourceMeta[row.dataSourceType as DataSourceType].type" effect="plain">
              {{ sourceMeta[row.dataSourceType as DataSourceType].label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusMeta[row.status as AgentStatus].type" effect="light" round>
              {{ statusMeta[row.status as AgentStatus].label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="owner" label="负责人" width="110" />
        <el-table-column label="最近更新" width="140">
          <template #default="{ row }">
            <span class="muted">{{ formatDateTime(row.updatedAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="auth.isAdmin" label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="removeAgent(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="没有找到匹配的智能体" :image-size="90">
            <el-button v-if="auth.isAdmin" type="primary" :icon="Plus" @click="openCreate">
              创建第一个智能体
            </el-button>
          </el-empty>
        </template>
      </el-table>
    </section>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="520px"
      align-center
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="例如：销售数据分析助手"
            maxlength="64"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="简要说明这个智能体解决什么问题"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <div class="form-grid">
          <el-form-item label="数据源类型" prop="dataSourceType">
            <el-select v-model="form.dataSourceType" class="full-width">
              <el-option
                v-for="type in allSourceTypes"
                :key="type"
                :label="sourceMeta[type].label"
                :value="type"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="form.status" class="full-width">
              <el-option label="运行中" value="RUNNING" />
              <el-option label="已暂停" value="PAUSED" />
              <el-option label="草稿" value="DRAFT" />
            </el-select>
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.agents-panel {
  overflow: hidden;
}

.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  padding: 18px 20px;
  border-bottom: 1px solid #f1f5f9;
}

.search-input {
  width: 280px;
}

.status-select {
  width: 140px;
}

.result-count {
  margin-left: auto;
  font-size: 12px;
  color: var(--da-muted);
}

.agents-table {
  width: 100%;
}

.agent-cell {
  display: flex;
  gap: 10px;
  align-items: center;
  min-width: 0;
}

.agent-avatar {
  display: grid;
  flex-shrink: 0;
  place-items: center;
  width: 36px;
  height: 36px;
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
  max-width: 380px;
  font-size: 11px;
  color: var(--da-muted);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0 16px;
}

.full-width {
  width: 100%;
}

@media (max-width: 720px) {
  .search-input {
    width: 100%;
  }

  .status-select {
    flex: 1;
    width: auto;
  }

  .result-count {
    width: 100%;
    margin-left: 0;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
